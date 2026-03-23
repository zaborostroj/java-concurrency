package org.example.concurrency.producer_consumer.volatile_spin_waiting;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicInteger;

@ToString
@EqualsAndHashCode
public class Buffer {
    private volatile Message[] messages;
    private volatile AtomicInteger writeIndex;
    private volatile AtomicInteger readIndex;

    public Buffer(int capacity) {
        this.messages = new Message[capacity];
        this.writeIndex = new AtomicInteger(0);
        this.readIndex = new AtomicInteger(0);
    }

    public void produce(Message message) {
        while (!Thread.currentThread().isInterrupted()) {
            int currentWriteIndex = writeIndex.get();
            int nextWriteIndex = (currentWriteIndex + 1) % messages.length;
            if (nextWriteIndex != readIndex.get()) {
                messages[currentWriteIndex] = message;
                writeIndex.set(nextWriteIndex);
                System.out.println("Thread " + Thread.currentThread().getName() + " is producing message: " + message);
                break;
            }
            Thread.onSpinWait();
        }
    }

    public Message consume() {
        while (!Thread.currentThread().isInterrupted()) {
            int currentReadIndex = readIndex.get();
            if (currentReadIndex != writeIndex.get()) {
                Message message = messages[currentReadIndex];
                readIndex.set((currentReadIndex + 1) % messages.length);
                System.out.println("Thread " + Thread.currentThread().getName() + " is consuming message: " + message);
                return message;
            }
            Thread.onSpinWait();
        }
        return null;
    }
}
