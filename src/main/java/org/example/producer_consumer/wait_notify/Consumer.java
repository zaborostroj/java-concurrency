package org.example.producer_consumer.wait_notify;

import java.util.Objects;

public class Consumer implements Runnable {
    private final Buffer buffer;

    public Consumer(Buffer buffer) {
        this.buffer = buffer;
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

            System.out.println("Consumer " + Thread.currentThread().getName() + " is trying to consume a message");
            Message message = buffer.consume();
            System.out.println("Consumer " + Thread.currentThread().getName() + " consumed a message " + message);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Consumer consumer = (Consumer) o;
        return Objects.equals(buffer, consumer.buffer);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(buffer);
    }

    @Override
    public String toString() {
        return "Consumer{" +
                "buffer=" + buffer +
                '}';
    }
}
