package org.example.concurrency.bounded_queue;

import org.example.concurrency.bounded_queue.circular_buffer.CircularBuffer;
import org.example.concurrency.bounded_queue.reentrant_lock_buffer.ReentrantLockBuffer;

public class BoundedQueuesDemo {
    public static void circularBufferDemo() {
        runProducerAndConsumer(new CircularBuffer(10), "CircularBuffer");
    }

    public static void reentrantLockBufferDemo() {
        runProducerAndConsumer(new ReentrantLockBuffer(10), "ReentrantLockBuffer");
    }

    private static void runProducerAndConsumer(BoundedQueueBuffer buffer, String threadNamePrefix) {
        BoundedQueueProducer producer = new BoundedQueueProducer(buffer);
        Thread producerThread = new Thread(producer, threadNamePrefix + "_producer");
        producerThread.start();

        BoundedQueueConsumer consumer = new BoundedQueueConsumer(buffer);
        Thread consumerThread = new Thread(consumer, threadNamePrefix + "_consumer");
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
