package com.trelix.trelix_app.service;

import java.util.UUID;

import com.trelix.trelix_app.dto.response.RefreshResult;
import com.trelix.trelix_app.entity.User;

public interface RefreshTokenService {
    String createRefreshToken(User user);

    RefreshResult validateAndRotate(String token);

    void revokeFamily(UUID familyId);

    void revokeAllUserTokens(UUID userId);
}
