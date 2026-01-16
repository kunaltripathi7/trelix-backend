package com.trelix.trelix_app.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank String refreshToken
) {}




