package com.example.demo.controller;

import com.example.demo.kafka.EmailMessage;
import com.example.demo.kafka.MessageProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "MessageController", description = "Контроллер сообщений в Kafka.")
public class MessageController {
    public final MessageProducer messageProducer;

    public MessageController(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @PostMapping("/sendMessage")
    @Operation(summary = "Отправить сообщение", description = "Отправление сообщения в Kafka.")
    public String sendMessage(@RequestBody EmailMessage message) {
        messageProducer.sendMessage(message);
        return "Сообщение отправлено в Kafka.";
    }
}
