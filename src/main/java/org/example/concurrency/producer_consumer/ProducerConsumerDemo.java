package org.example.concurrency.producer_consumer;

import org.example.concurrency.producer_consumer.lock_condition.Buffer;
import org.example.concurrency.producer_consumer.lock_condition.Consumer;
import org.example.concurrency.producer_consumer.lock_condition.Producer;

public class ProducerConsumerDemo {
    public static void runWaitNotify() {
        System.out.println("\nWait-notify producer-consumer demo");

        org.example.concurrency.producer_consumer.wait_notify.Buffer buffer = new org.example.concurrency.producer_consumer.wait_notify.Buffer(5);
        org.example.concurrency.producer_consumer.wait_notify.Consumer consumer = new org.example.concurrency.producer_consumer.wait_notify.Consumer(buffer);
        org.example.concurrency.producer_consumer.wait_notify.Producer firstProducer = new org.example.concurrency.producer_consumer.wait_notify.Producer(buffer);
        org.example.concurrency.producer_consumer.wait_notify.Producer secondProducer = new org.example.concurrency.producer_consumer.wait_notify.Producer(buffer);

        Thread consumerThread = new Thread(consumer, "Consumer");
        consumerThread.start();

        Thread firstProducerThread = new Thread(firstProducer, "First Producer");
        firstProducerThread.start();

        Thread secondProducerThread = new Thread(secondProducer, "Second Producer");
        secondProducerThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            consumerThread.interrupt();
            firstProducerThread.interrupt();
            secondProducerThread.interrupt();

            try {
                consumerThread.join();
                firstProducerThread.join();
                secondProducerThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void runLockWithCondition() {
        System.out.println("\nLock with condition producer-consumer demo");

        Buffer buffer = new Buffer(5);
        Consumer consumer = new Consumer(buffer);
        Producer producer1 = new Producer(buffer);
        Producer producer2 = new Producer(buffer);

        Thread consumerThread = new Thread(consumer, "Consumer");
        consumerThread.start();

        Thread firstProducerThread = new Thread(producer1, "Producer1");
        firstProducerThread.start();

        Thread secondProducerThread = new Thread(producer2, "Producer2");
        secondProducerThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            consumerThread.interrupt();
            firstProducerThread.interrupt();
            secondProducerThread.interrupt();

            try {
                consumerThread.join();
                firstProducerThread.join();
                secondProducerThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void runSempahore() {
        System.out.println("\nSemaphore producer-consumer demo");

        org.example.concurrency.producer_consumer.semaphore.Buffer buffer = new org.example.concurrency.producer_consumer.semaphore.Buffer(5);
        org.example.concurrency.producer_consumer.semaphore.Consumer consumer = new org.example.concurrency.producer_consumer.semaphore.Consumer(buffer);
        org.example.concurrency.producer_consumer.semaphore.Producer producer1 = new org.example.concurrency.producer_consumer.semaphore.Producer(buffer);
        org.example.concurrency.producer_consumer.semaphore.Producer producer2 = new org.example.concurrency.producer_consumer.semaphore.Producer(buffer);

        Thread consumerThread = new Thread(consumer, "Consumer");
        consumerThread.start();

        Thread firstProducerThread = new Thread(producer1, "Producer1");
        firstProducerThread.start();

        Thread secondProducerThread = new Thread(producer2, "Producer2");
        secondProducerThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            consumerThread.interrupt();
            firstProducerThread.interrupt();
            secondProducerThread.interrupt();
            try {
                consumerThread.join();
                firstProducerThread.join();
                secondProducerThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
