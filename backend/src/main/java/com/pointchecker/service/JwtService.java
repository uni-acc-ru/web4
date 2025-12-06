package com.pointchecker.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.ejb.Stateless;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Stateless
public class JwtService {
    
    private static final String JWT_SECRET = "secretsecretsecretsecretsecret256bitskey";
    private static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000;
    
    public JwtService() {
    }
    
    public String generateToken(Long userId) {
        SecretKey key = getSigningKey();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);
        
        return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("userId", userId)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact();
    }
    
    public Long validateTokenAndGetUserId(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        try {
            SecretKey key = getSigningKey();
            
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            return claims.get("userId", Long.class);
            
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean isTokenExpired(String token) {
        try {
            SecretKey key = getSigningKey();
            
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            return claims.getExpiration().before(new Date());
            
        } catch (Exception e) {
            return true;
        }
    }
    
    public Claims getClaimsFromToken(String token) {
        try {
            SecretKey key = getSigningKey();
            
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
                
        } catch (Exception e) {
            return null;
        }
    }
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    }
}
