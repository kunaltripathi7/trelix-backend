package com.trelix.trelix_app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.trelix.trelix_app.entity.RefreshToken;

import jakarta.transaction.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Transactional
    @Query("Update RefreshToken SET revoked=true WHERE familyId = ?1")
    void revokeAllRefreshTokens(UUID familyId);

    @Modifying
    @Transactional
    @Query("Update RefreshToken SET revoked=true WHERE user.id = ?1")
    void revokeAllByUserId(UUID userId);

    @Modifying
    @Transactional
    @Query("Delete from RefreshToken where expiresAt < ?1")
    void deleteExpiredTokens(LocalDateTime now);

    List<RefreshToken> findByUserId(UUID userId);

}
