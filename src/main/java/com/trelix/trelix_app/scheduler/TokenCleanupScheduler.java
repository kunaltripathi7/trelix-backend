package com.trelix.trelix_app.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.trelix.trelix_app.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 3 * * ?") // 3 am
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

}
