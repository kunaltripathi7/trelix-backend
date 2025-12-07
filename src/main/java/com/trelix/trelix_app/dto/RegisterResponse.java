package com.trelix.trelix_app.dto;

public record RegisterResponse(
        UserResponse user,
        String message
) {}
