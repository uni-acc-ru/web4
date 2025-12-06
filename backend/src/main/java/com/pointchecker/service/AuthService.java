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
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AuthService {
    
    @jakarta.ejb.EJB
    private UserRepository userRepository;
    
    @jakarta.ejb.EJB
    private JwtService jwtService;
    
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
        
        String sessionToken = jwtService.generateToken(user.getId());
        
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
        
        String sessionToken = jwtService.generateToken(user.getId());
        return new AuthResponse(user.getId(), user.getUsername(), sessionToken);
    }
    
    public Long validateSession(String sessionToken) {
        return jwtService.validateTokenAndGetUserId(sessionToken);
    }
}
