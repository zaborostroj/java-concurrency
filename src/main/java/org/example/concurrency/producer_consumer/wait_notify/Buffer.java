package org.example.concurrency.producer_consumer.wait_notify;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

@ToString
@EqualsAndHashCode
public class Buffer {
    private final List<Message> messages;
    private final int capacity;
    private final Object lock;

    public Buffer(int capacity) {
        this.messages = new LinkedList<>();
        this.capacity = capacity;
        this.lock = new Object();
    }

    public void produce(Message message) {
        synchronized (lock) {
            while (messages.size() == capacity) {
                try {
                    System.out.println("Buffer " + Thread.currentThread().getName() + " is full, waiting for space");
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            messages.add(message);
            lock.notifyAll();
        }
    }

    public Message consume() {
        synchronized (lock) {
            while (messages.isEmpty()) {
                try {
                    System.out.println("Buffer " + Thread.currentThread().getName() + " is empty, waiting for data");
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            Message result = messages.removeFirst();
            lock.notifyAll();
            return result;
        }
    }
}
