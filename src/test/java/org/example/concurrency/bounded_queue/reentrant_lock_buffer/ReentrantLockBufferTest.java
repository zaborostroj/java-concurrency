package org.example.concurrency.bounded_queue.reentrant_lock_buffer;

import org.example.concurrency.bounded_queue.QueueMessage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class ReentrantLockBufferTest {
    @Test
    public void testBufferIsFIFO() {
        ReentrantLockBuffer buffer = new ReentrantLockBuffer(3);

        QueueMessage expectedMessage1 = new QueueMessage("1");
        buffer.produce(expectedMessage1);
        QueueMessage expectedMessage2 = new QueueMessage("2");
        buffer.produce(expectedMessage2);

        QueueMessage consumedMessage1 = (QueueMessage) buffer.consume();
        QueueMessage consumedMessage2 = (QueueMessage) buffer.consume();

        assertEquals(consumedMessage1, expectedMessage1);
        assertEquals(consumedMessage2, expectedMessage2);
    }

    @Test
    public void testBlockOnFull() {
        ReentrantLockBuffer buffer = new ReentrantLockBuffer(2);

        QueueMessage message = new QueueMessage("1");
        buffer.produce(message);
        buffer.produce(message);

        Thread producerThread = new Thread(() -> buffer.produce(message));
        producerThread.start();

        await().atMost(1, TimeUnit.SECONDS).until(() -> Thread.State.WAITING.equals(producerThread.getState()));

        producerThread.interrupt();
        try {
            producerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testBlockOnEmpty() {
        ReentrantLockBuffer buffer = new ReentrantLockBuffer(2);

        Thread consumerThread = new Thread(() -> {
            var ignored = buffer.consume();
        });
        consumerThread.start();

        await().atMost(1, TimeUnit.SECONDS).until(() -> Thread.State.WAITING.equals(consumerThread.getState()));

        consumerThread.interrupt();
        try {
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSingleConsumerFIFO() {
        int messagesCount = 1000;
        ReentrantLockBuffer buffer = new ReentrantLockBuffer(10);
        List<String> results = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(messagesCount);

        Thread producerThread = new Thread(() -> {
            for (int i = 0; i < messagesCount; i++) {
                buffer.produce(new QueueMessage("" + i));
            }
        });

        Thread consumerThread = new Thread(() -> {
            for (int i = 0; i < messagesCount; i++) {
                QueueMessage msg = (QueueMessage) buffer.consume();
                results.add(msg.getName());
                latch.countDown();
            }
        });

        producerThread.start();
        consumerThread.start();

        try {
            boolean completed = latch.await(10, TimeUnit.SECONDS);
            assertTrue(completed, "Consumer did not finish in time");
            assertEquals(messagesCount, results.size());
            for (int i = 0; i < messagesCount; i++) {
                assertEquals("" + i, results.get(i), "Wrong message order at index " + i);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Test interrupted");
        } finally {
            producerThread.interrupt();
            consumerThread.interrupt();
        }
    }

    @Test
    public void testMultiConsumerFIFO() {
        int messagesCount = 1000;
        ReentrantLockBuffer buffer = new ReentrantLockBuffer(50);
        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
        CountDownLatch latch = new CountDownLatch(messagesCount);

        Thread producerThread = new Thread(() -> {
            for (int i = 0; i < messagesCount; i++) {
                buffer.produce(new QueueMessage("" + i));
            }
        });
        producerThread.start();

        Runnable consumerRunnable = () -> {
            while (!Thread.currentThread().isInterrupted() && latch.getCount() > 0) {
                QueueMessage msg = (QueueMessage) buffer.consume();
                if (msg == null) {
                    break;  // Graceful exit on interrupt
                }
                results.add(msg.getName());
                latch.countDown();
            }
        };

        Thread consumerThread1 = new Thread(consumerRunnable);
        Thread consumerThread2 = new Thread(consumerRunnable);
        Thread consumerThread3 = new Thread(consumerRunnable);
        Thread consumerThread4 = new Thread(consumerRunnable);
        Thread consumerThread5 = new Thread(consumerRunnable);
        consumerThread1.start();
        consumerThread2.start();
        consumerThread3.start();
        consumerThread4.start();
        consumerThread5.start();

        try {
            producerThread.join(30000);

            // Wait for all consumers
            boolean completed = latch.await(10, TimeUnit.SECONDS);
            assertTrue(completed, "Consumers did not finish in time");
            assertEquals(messagesCount, results.size());
            List<String> resultsList = new ArrayList<>(results);
            assertEquals(messagesCount, (int) resultsList.stream().distinct().count(), "Should have unique messages");
            for (int i = 0; i < messagesCount; i++) {
                assertTrue(results.contains("" + i), "Missing message " + i);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Test interrupted");
        } finally {
            Stream.of(consumerThread1, consumerThread2, consumerThread3, consumerThread4, consumerThread5)
                    .forEach(Thread::interrupt);
            Stream.of(consumerThread1, consumerThread2, consumerThread3, consumerThread4, consumerThread5)
                    .forEach(t -> {
                        try {
                            t.join(5000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    });
        }
    }
}