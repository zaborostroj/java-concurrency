package org.example.producer_consumer;

import org.example.producer_consumer.wait_notify.Buffer;
import org.example.producer_consumer.wait_notify.Consumer;
import org.example.producer_consumer.wait_notify.Producer;

public class ProducerConsumerDemo {
    public static void runWaitNotify() {
        System.out.println( "Wait-notify producer-consumer demo");

        Buffer buffer = new Buffer(5);
        Consumer consumer = new Consumer(buffer);
        Producer firstProducer = new Producer(buffer);
        Producer secondProducer = new Producer(buffer);

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
}
