package org.example.concurrency.producer_consumer.lock_condition;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
public class Message {
    private final UUID uuid;
    private final String content;

    public Message(String content) {
        this.uuid = UUID.randomUUID();
        this.content = content;
    }
}