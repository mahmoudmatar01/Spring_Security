package org.example.spring_security.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.spring_security.entity.User;
import org.example.spring_security.service.JWTService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTServiceImpl implements JWTService {

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @Override
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // one day
                .setIssuer("app-service")
                .claim("created", Calendar.getInstance().getTime())
                .claim("user_id", ((User) userDetails).getId())
                .claim("user_role", ((User) userDetails).getRole().toString())
                .claim("user_email", ((User) userDetails).getEmail())
                .signWith(getSignKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(Map<String,Object> extractedClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extractedClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 604800000)) // 7 days
                .signWith(getSignKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey(){
        byte[] key = Decoders.BASE64.decode(String.valueOf(jwtSigningKey));
        return Keys.hmacShaKeyFor(key);
    }

    @Override
    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    @Override
    public String extractUserEmail(String token){
        Claims claims=extractAllClaims(token);
        return claims.get("user_email", String.class);
    }

    private <T> T extractClaim(String token, Function<Claims,T>claimsTResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsTResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    @Override
    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username= extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token){
        return extractClaim(token,Claims::getExpiration).before(new Date());
    }
}
