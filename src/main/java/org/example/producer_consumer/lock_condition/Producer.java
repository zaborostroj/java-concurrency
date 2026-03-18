package org.example.producer_consumer.lock_condition;

import java.util.Objects;
import java.util.Random;

public class Producer implements Runnable {
    private final Buffer buffer;
    private final Random random;

    public Producer(Buffer buffer) {
        this.buffer = buffer;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            Message message = new Message(Thread.currentThread().getName() + " " + random.nextInt());
            System.out.println("Producer " + Thread.currentThread().getName() + " is trying to put a new message " + message);
            buffer.produce(message);
            System.out.println("Producer " + Thread.currentThread().getName() + " successfully put a new message");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Producer producer = (Producer) o;
        return Objects.equals(buffer, producer.buffer);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(buffer);
    }

    @Override
    public String toString() {
        return "Producer{" +
                "buffer=" + buffer +
                '}';
    }
}
