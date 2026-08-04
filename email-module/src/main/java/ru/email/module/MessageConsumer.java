package ru.email.module;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
    @KafkaListener(topics = "messages", groupId = "demo-group")
        public void receiveMessage(String message) {
        System.out.println("Получено из Kafka: " + message);
    }
}
