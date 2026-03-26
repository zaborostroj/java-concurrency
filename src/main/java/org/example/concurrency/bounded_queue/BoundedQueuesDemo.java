package org.example.concurrency.bounded_queue;

import org.example.concurrency.bounded_queue.circular_buffer.CircularBuffer;

public class BoundedQueuesDemo {
    public static void circularBufferDemo() {
        BoundedQueueBuffer buffer = new CircularBuffer(10);

        BoundedQueueProducer producer = new BoundedQueueProducer(buffer);
        Thread producerThread = new Thread(producer);
        producerThread.start();

        BoundedQueueConsumer consumer = new BoundedQueueConsumer(buffer);
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            producerThread.interrupt();
            consumerThread.interrupt();
            try {
                producerThread.join();
                consumerThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
