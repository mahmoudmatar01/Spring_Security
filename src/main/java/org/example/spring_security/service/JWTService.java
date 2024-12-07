package org.example.spring_security.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JWTService {
    String generateToken(UserDetails userDetails);
    String generateRefreshToken(Map<String,Object> extractedClaims, UserDetails userDetails);
    String extractUsername(String token);
    String extractUserEmail(String token);
    boolean isTokenValid(String token,UserDetails userDetails);
}
