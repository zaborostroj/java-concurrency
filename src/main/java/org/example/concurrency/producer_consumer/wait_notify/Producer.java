package org.example.concurrency.producer_consumer.wait_notify;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Random;

@ToString
@EqualsAndHashCode
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
}
