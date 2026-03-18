package org.example.producer_consumer.semaphore;

import java.util.Objects;
import java.util.UUID;

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

    public String toString() {
        return "Message{uuid=" + uuid + ", content='" + content + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(uuid, message.uuid) && Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, content);
    }
}
