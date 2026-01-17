package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.common.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "trelix-notifications";

    public void sendNotification(NotificationEvent event) {
        log.info("Sending notification event to Kafka: {}", event);
        kafkaTemplate.send(TOPIC, event);
    }
}
