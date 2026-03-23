package org.example.concurrency.producer_consumer.lock_condition;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@ToString
@EqualsAndHashCode
public class Message {
    private UUID uuid;
    private String content;

    public Message(String content) {
        this.uuid = UUID.randomUUID();
        this.content = content;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getContent() {
        return content;
    }
}
