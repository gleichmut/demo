package com.example.demo.controller;

import com.example.demo.kafka.MessageProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    public final MessageProducer messageProducer;

    public MessageController(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @PostMapping("/sendMessage")
    public String sendMessage(@RequestBody String message) {
        messageProducer.sendMessage(message);
        return "Сообщение отправлено в Kafky";
    }
}
