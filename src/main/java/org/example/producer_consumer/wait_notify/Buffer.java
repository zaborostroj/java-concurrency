package org.example.producer_consumer.wait_notify;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Buffer buffer = (Buffer) o;
        return capacity == buffer.capacity && Objects.equals(messages, buffer.messages) && Objects.equals(lock, buffer.lock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messages, capacity, lock);
    }

    @Override
    public String toString() {
        return "Buffer{" +
                "messages=" + messages +
                ", capacity=" + capacity +
                ", lock=" + lock +
                '}';
    }
}
