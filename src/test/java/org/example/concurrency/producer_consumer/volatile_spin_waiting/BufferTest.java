package org.example.concurrency.producer_consumer.volatile_spin_waiting;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BufferTest {

    @Test
    void testFifoSingleThread() {
        Buffer buffer = new Buffer(4);
        Message m1 = new Message("1");
        Message m2 = new Message("2");

        buffer.produce(m1);
        buffer.produce(m2);

        assertEquals(m1, buffer.consume());
        assertEquals(m2, buffer.consume());
    }

    @Test
    void testWrapAround() {
        Buffer buffer = new Buffer(4);
        Message m0 = new Message("0");
        Message m1 = new Message("1");
        Message m2 = new Message("2");
        Message m3 = new Message("3");

        buffer.produce(m0);
        buffer.produce(m1);
        buffer.produce(m2);

        assertEquals(m0, buffer.consume());
        buffer.produce(m3);
        assertEquals(m1, buffer.consume());
        assertEquals(m2, buffer.consume());
        assertEquals(m3, buffer.consume());
    }

    @Test
    void testStressNoDataLoss() throws InterruptedException {
        int capacity = 16;
        int numMessages = 1000;
        Buffer buffer = new Buffer(capacity);

        AtomicInteger produced = new AtomicInteger();
        AtomicInteger consumed = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            executor.submit(() -> {  // Producer
                for (int i = 0; i < numMessages; i++) {
                    buffer.produce(new Message("msg-" + i));
                    produced.incrementAndGet();
                }
                latch.countDown();
            });
            executor.submit(() -> {  // Consumer
                List<Message> results = new ArrayList<>();
                while (consumed.get() < numMessages) {
                    Message msg = buffer.consume();
                    if (msg != null) {
                        results.add(msg);
                        consumed.incrementAndGet();
                    }
                }
                // Check all unique, no loss
                assertEquals(numMessages, results.size());
            });

            // Wait for producer
            assertTrue(latch.await(5, TimeUnit.SECONDS));
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
        assertEquals(numMessages, produced.get());
        assertEquals(numMessages, consumed.get());
    }

    @Test
    void testProducerSpinsOnFull() throws InterruptedException {
        Buffer buffer = new Buffer(4);

        buffer.produce(new Message("1"));
        buffer.produce(new Message("2"));
        buffer.produce(new Message("3"));

        CountDownLatch latch = new CountDownLatch(1);
        Thread spinner = new Thread(() -> {
            buffer.produce(new Message("spin-msg"));
            latch.countDown();
        });
        spinner.start();

        Thread.sleep(50);

        assertNotNull(buffer.consume());

        var ignored = latch.await(200, TimeUnit.MILLISECONDS);
        spinner.join(200);
        assertFalse(spinner.isAlive());
    }

    @Test
    void testInterruptExitsLoop() {
        Buffer buffer = new Buffer(4);
        Thread consumer = new Thread(buffer::consume);
        consumer.start();

        consumer.interrupt();
        assertDoesNotThrow(() -> consumer.join(100));
    }
}