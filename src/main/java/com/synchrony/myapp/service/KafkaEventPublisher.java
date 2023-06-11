package com.synchrony.myapp.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventPublisher {

    private static final String TOPIC_NAME = "image-events";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(String username, String imageName) {
        String eventMessage = "Username: " + username + ", Image Name: " + imageName;
        kafkaTemplate.send(TOPIC_NAME, eventMessage);
    }
}

