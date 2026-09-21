package com.example.demo.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {
    public final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    public MessageProducer(KafkaTemplate<String, EmailMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(EmailMessage emailMessage) {
        kafkaTemplate.send("email-messages-json", emailMessage);
        System.out.println("Сообщение отправлено: " + emailMessage);
    }
}
