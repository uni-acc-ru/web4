package com.pointchecker.dto;

public class AuthResponse {
    private Long userId;
    private String username;
    private String sessionToken;
    
    public AuthResponse() {}
    
    public AuthResponse(Long userId, String username, String sessionToken) {
        this.userId = userId;
        this.username = username;
        this.sessionToken = sessionToken;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getSessionToken() {
        return sessionToken;
    }
    
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
