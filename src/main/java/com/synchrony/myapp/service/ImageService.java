package com.synchrony.myapp.service;

import org.springframework.stereotype.Service;

@Service
public class ImageService {

    private final KafkaEventPublisher kafkaEventPublisher;

    public ImageService(KafkaEventPublisher kafkaEventPublisher) {
        this.kafkaEventPublisher = kafkaEventPublisher;
    }

    public void uploadImage(String username, String imageName) {
        // Your upload image logic here
        // ...

        // Publish the messaging event
        kafkaEventPublisher.publishEvent(username, imageName);
    }

    // Other methods
}

