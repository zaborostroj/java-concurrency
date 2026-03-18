package org.example.producer_consumer.lock_condition;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {
    private final ReentrantLock lock;
    private final Condition notFull;
    private final Condition notEmpty;
    private final List<Message> messages;
    private final int capacity;

    public Buffer(int capacity) {
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
        this.messages = new LinkedList<>();
        this.capacity = capacity;
    }

    public void produce(Message message) {
        lock.lock();
        try {
            while (capacity == messages.size()) {
                try {
                    System.out.println("Buffer " + Thread.currentThread().getName() + " is full, waiting for space");
                    notFull.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            System.out.println("Buffer " + Thread.currentThread().getName() + " is trying to produce message");
            messages.add(message);
            System.out.println("Buffer " + Thread.currentThread().getName() + " produced message: " + message);

            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public Message consume() {
        lock.lock();
        try {
            while (messages.isEmpty()) {
                try {
                    System.out.println("Buffer " + Thread.currentThread().getName() + " is empty, waiting for messages");
                    notEmpty.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }

            System.out.println("Buffer " + Thread.currentThread().getName() + " is trying to consume message");
            Message message = messages.removeFirst();
            System.out.println("Buffer " + Thread.currentThread().getName() + " consumed message: " + message);

            notFull.signal();
            return message;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Buffer buffer = (Buffer) o;
        return capacity == buffer.capacity && Objects.equals(lock, buffer.lock) && Objects.equals(messages, buffer.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lock, messages, capacity);
    }

    @Override
    public String toString() {
        return "Buffer{" +
                "lock=" + lock +
                ", messages=" + messages +
                ", capacity=" + capacity +
                '}';
    }
}
