package com.Backend.dto;

public class AuthResponse {
    private String token;
    private Long userId;
    private String name;

    public AuthResponse(String token, Long userId, String name) {
        this.token = token;
        this.userId = userId;
        this.name = name;
    }

    // Getters
    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
}