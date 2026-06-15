package org.example.concurrency.bounded_queue;

import java.util.Random;

public class BoundedQueueProducer implements Runnable {
    private final BoundedQueueBuffer buffer;
    private final Random random;

    public BoundedQueueProducer(BoundedQueueBuffer buffer) {
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

            QueueMessage message = new QueueMessage(Thread.currentThread().getName() + " " + random.nextInt());
            System.out.println("Producer " + Thread.currentThread().getName() + " is trying to put an object to queue " + message);
            buffer.produce(message);
            System.out.println("Producer " + Thread.currentThread().getName() + " successfully put a new object to queue");
        }
    }
}
