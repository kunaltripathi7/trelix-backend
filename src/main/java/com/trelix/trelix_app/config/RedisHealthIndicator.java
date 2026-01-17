package com.trelix.trelix_app.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public Health health() {
        try {
            String pong = redisConnectionFactory.getConnection().ping();
            if ("PONG".equals(pong)) {
                return Health.up()
                        .withDetail("status", "Redis is responding")
                        .build();
            }
            return Health.down()
                    .withDetail("status", "Redis ping failed")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
