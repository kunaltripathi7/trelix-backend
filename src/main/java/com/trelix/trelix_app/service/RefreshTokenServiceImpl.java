package com.trelix.trelix_app.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.trelix.trelix_app.dto.response.RefreshResult;
import com.trelix.trelix_app.entity.RefreshToken;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.exception.UnauthorizedException;
import com.trelix.trelix_app.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refresh-token-expiration-days:7}")
    private int refreshTokenExpirationDays;

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public String createRefreshToken(User user) {
        return createRefreshToken(user, UUID.randomUUID());
    }

    @Override
    public RefreshResult validateAndRotate(String rawToken) {
        String tokenHash = hashToken(rawToken);

        RefreshToken existingToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(
                        () -> new UnauthorizedException("Invalid refresh token", ErrorCode.AUTHENTICATION_FAILURE));

        if (existingToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Expired refresh token", ErrorCode.AUTHENTICATION_FAILURE);
        }

        if (existingToken.isRevoked()) {
            revokeFamily(existingToken.getFamilyId());
            throw new UnauthorizedException("Token reuse detected - session revoked", ErrorCode.AUTHENTICATION_FAILURE);
        }

        User user = existingToken.getUser();
        existingToken.setRevoked(true);
        refreshTokenRepository.save(existingToken);

        String newToken = createRefreshToken(user, existingToken.getFamilyId());
        return new RefreshResult(newToken, user);
    }

    @Override
    public void revokeFamily(UUID familyId) {
        refreshTokenRepository.revokeAllRefreshTokens(familyId);
    }

    private String createRefreshToken(User user, UUID familyId) {
        String rawToken = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(rawToken))
                .familyId(familyId)
                .expiresAt(LocalDateTime.now().plusDays(refreshTokenExpirationDays))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
