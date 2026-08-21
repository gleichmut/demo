package com.example.demo.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailMessage {
    public String from;
    public String to;
    public String message;

    @Override
    public String toString() {
        return "EmailMessage{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}