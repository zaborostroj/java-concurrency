package org.example.concurrency.producer_consumer.semaphore;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

@ToString
@EqualsAndHashCode
public class Buffer {
    private final List<Message> messages;
    private final int capacity;
    private final Semaphore freeSlots;
    private final Semaphore occupiedSlots;
    private final Semaphore mutex;

    public Buffer(int capacity) {
        this.messages = new LinkedList<>();
        this.capacity = capacity;
        this.freeSlots = new Semaphore(capacity);
        this.occupiedSlots = new Semaphore(0);
        this.mutex = new Semaphore(1);
    }

    public void produce(Message message) {
        try {
            System.out.println("Buffer " + Thread.currentThread().getName() + " waiting for free slot");
            freeSlots.acquire();
            mutex.acquire();

            try {
                System.out.println("Buffer " + Thread.currentThread().getName() + " is trying to produce message");
                messages.add(message);
                System.out.println("Buffer " + Thread.currentThread().getName() + " produced message: " + message);
                occupiedSlots.release();
            } finally {
                mutex.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Message consume() {
        try {
            System.out.println("Buffer " + Thread.currentThread().getName() + " waiting for free slot");
            occupiedSlots.acquire();
            mutex.acquire();

            try {
                System.out.println("Buffer " + Thread.currentThread().getName() + " is trying to consume message");
                Message message = messages.removeFirst();
                System.out.println("Buffer " + Thread.currentThread().getName() + " consumed message: " + message);
                freeSlots.release();
                return message;
            } finally {
                mutex.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
