package com.pointchecker.service;

import com.pointchecker.dto.AuthResponse;
import com.pointchecker.dto.LoginRequest;
import com.pointchecker.dto.RegisterRequest;
import com.pointchecker.model.User;
import com.pointchecker.repository.UserRepository;
import com.pointchecker.validator.AuthValidator;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AuthService {
    
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
        userRepository.getEntityManager().flush();
        
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
            String decoded = new String(Base64.getDecoder().decode(sessionToken));
            String[] parts = decoded.split(":");
            if (parts.length == 2) {
                return Long.parseLong(parts[0]);
            }
        } catch (Exception e) {
            return null;
        }
        
        return null;
    }
    
    private String generateSessionToken(Long userId) {
        String token = userId + ":" + UUID.randomUUID().toString();
        return Base64.getEncoder().encodeToString(token.getBytes());
    }
}
