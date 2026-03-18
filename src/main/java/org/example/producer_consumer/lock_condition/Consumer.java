package org.example.producer_consumer.lock_condition;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
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
}
