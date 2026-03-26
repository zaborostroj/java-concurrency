package org.example.concurrency.bounded_queue;

public class BoundedQueueConsumer implements Runnable {
    private final BoundedQueueBuffer buffer;

    public BoundedQueueConsumer(BoundedQueueBuffer buffer) {
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

            System.out.println("Consumer " + Thread.currentThread().getName() + " is trying to get an object");
            var message = buffer.consume();
            System.out.println("Consumer " + Thread.currentThread().getName() + " consumed an object " + message);
        }
    }
}
