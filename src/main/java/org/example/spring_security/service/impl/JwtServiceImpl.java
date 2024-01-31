package org.example.spring_security.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.spring_security.entity.UserData;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@Service
public class JwtServiceImpl {
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setIssuer("app-service")
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 24 * 1000))
                .claim("created", Calendar.getInstance().getTime())
                .claim("userId", ((UserData) userDetails).getId())
                .claim("userRole", ((UserData) userDetails).getRole().toString())
                .claim("userEmail", ((UserData) userDetails).getEmail())
                .signWith(SECRET_KEY)
                .compact();
    }

    public String getUserNameFromToken(String token){
        Claims claims=getClaims(token);
        return claims.getSubject();
    }
    public String getUserEmailFromToken(String token){
        Claims claims=getClaims(token);
        return claims.get("userEmail", String.class);
    }


    public String getTokenIdFromToken(String token){
        Claims claims=getClaims(token);
        return claims.getId();
    }

    public boolean isTokenValid(String token, UserDetails user){
        String userName=getUserNameFromToken(token);
        return userName.equalsIgnoreCase(user.getUsername())&&!isTokenExpired(token);
    }

    public boolean isTokenExpired(String token){
        Date expiration=getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }



    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
