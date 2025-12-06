package com.pointchecker.service;

import com.pointchecker.dto.AuthResponse;
import com.pointchecker.dto.LoginRequest;
import com.pointchecker.dto.RegisterRequest;
import com.pointchecker.model.User;
import com.pointchecker.repository.UserRepository;
import com.pointchecker.validator.AuthValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AuthService {
    
    // JWT Secret Key - В production должен быть в переменных окружения!
    private static final String JWT_SECRET = "your-super-secret-jwt-key-that-must-be-at-least-256-bits-long-for-hs256";
    private static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 часа
    
    @jakarta.ejb.EJB
    private UserRepository userRepository;
    
    public AuthService() {
    }
    
    public AuthResponse register(RegisterRequest request) throws Exception {
        String usernameError = AuthValidator.validateUsername(request.getUsername());
        if (usernameError != null) {
            throw new Exception(usernameError);
        }
        
        String passwordError = AuthValidator.validatePassword(request.getPassword());
        if (passwordError != null) {
            throw new Exception(passwordError);
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new Exception("Username already exists");
        }
        
        String passwordHash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        User user = new User(request.getUsername(), passwordHash);
        user = userRepository.save(user);
        
        String sessionToken = generateSessionToken(user.getId());
        
        return new AuthResponse(user.getId(), user.getUsername(), sessionToken);
    }
    
    public AuthResponse login(LoginRequest request) throws Exception {
        String usernameError = AuthValidator.validateUsername(request.getUsername());
        if (usernameError != null) {
            throw new Exception("Invalid username or password");
        }
        
        String passwordError = AuthValidator.validatePassword(request.getPassword());
        if (passwordError != null) {
            throw new Exception("Invalid username or password");
        }
        
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (!userOpt.isPresent()) {
            throw new Exception("Invalid username or password");
        }
        
        User user = userOpt.get();
        if (!BCrypt.checkpw(request.getPassword(), user.getPasswordHash())) {
            throw new Exception("Invalid username or password");
        }
        
        String sessionToken = generateSessionToken(user.getId());
        return new AuthResponse(user.getId(), user.getUsername(), sessionToken);
    }
    
    public Long validateSession(String sessionToken) {
        if (sessionToken == null || sessionToken.isEmpty()) {
            return null;
        }
        
        try {
            SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
            
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(sessionToken)
                .getPayload();
            
            // Проверяем, что токен не истек (парсер это делает автоматически)
            Long userId = claims.get("userId", Long.class);
            return userId;
            
        } catch (Exception e) {
            // Токен невалидный или истек
            return null;
        }
    }
    
    private String generateSessionToken(Long userId) {
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        
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
}
