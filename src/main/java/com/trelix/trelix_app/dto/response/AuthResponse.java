package com.trelix.trelix_app.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {}




