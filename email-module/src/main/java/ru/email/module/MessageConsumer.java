package ru.email.module;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.example.demo.kafka.EmailMessage;

@Service
public class MessageConsumer {
    @KafkaListener(topics = "email-messages-json", groupId = "demo-group")
        public void receiveMessage(EmailMessage emailMessage) {
        System.out.println("Получено из Kafka: " + emailMessage);
    }

}
