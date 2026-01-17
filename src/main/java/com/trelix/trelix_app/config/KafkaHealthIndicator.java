package com.trelix.trelix_app.config;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaHealthIndicator implements HealthIndicator {

    private final KafkaAdmin kafkaAdmin;

    @Override
    public Health health() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            String clusterId = adminClient.describeCluster()
                    .clusterId()
                    .get(5, TimeUnit.SECONDS);

            return Health.up()
                    .withDetail("clusterId", clusterId)
                    .withDetail("status", "Kafka broker is available")
                    .build();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return Health.down()
                    .withDetail("error", "Kafka broker unavailable: " + e.getMessage())
                    .build();
        }
    }
}
