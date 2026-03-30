package org.example.concurrency.bounded_queue.lock_free_buffer;

import lombok.AllArgsConstructor;
import org.example.concurrency.bounded_queue.BoundedQueueBuffer;
import org.example.concurrency.bounded_queue.QueueMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LockFreeBufferTest {

    @Test
    public void testSPSC() {
        BoundedQueueBuffer buffer = new LockFreeBuffer(2);
        var expectedMessage1 = new QueueMessage("message1");
        var expectedMessage2 = new QueueMessage("message2");
        buffer.offer(expectedMessage1);
        buffer.offer(expectedMessage2);
        var actualMessage1 = (QueueMessage) buffer.poll();
        var actualMessage2 = (QueueMessage) buffer.poll();
        assertEquals(expectedMessage1.getName(), actualMessage1.getName());
        assertEquals(expectedMessage2.getName(), actualMessage2.getName());
    }

    static Stream<Arguments> mpmcScenarios() {
        return Stream.of(
                Arguments.of(1, 1, 1_000, 1),
                Arguments.of(2, 2, 1_000, 2),
                Arguments.of(3, 3, 1_000, 10),
                Arguments.of(4, 2, 2_000, 4),
                Arguments.of(2, 4, 500, 8),
                Arguments.of(6, 6, 300, 16)
        );
    }

    @ParameterizedTest(name = "producers={0}, consumers={1}, itemsPerProducer={2}, capacity={3}")
    @MethodSource("mpmcScenarios")
    void testMPMC(int producerCount, int consumerCount, int itemsPerProducer, int capacity) throws InterruptedException {
        BoundedQueueBuffer buffer = new LockFreeBuffer(capacity);

        int totalItems = producerCount * itemsPerProducer;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(producerCount + consumerCount);

        AtomicInteger consumed = new AtomicInteger(0);
        AtomicIntegerArray seen = new AtomicIntegerArray(totalItems);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        AtomicBoolean stop = new AtomicBoolean(false);

        for (int producerId = 0; producerId < producerCount; producerId++) {
            final int id = producerId;
            Thread producer = new Thread(() -> {
                try {
                    startLatch.await();

                    int from = id * itemsPerProducer;
                    int to = from + itemsPerProducer;

                    for (int value = from; value < to && !stop.get(); ) {
                        if (buffer.offer(value)) {
                            value++;
                        } else {
                            Thread.onSpinWait();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    failure.compareAndSet(null, e);
                    stop.set(true);
                } catch (Throwable t) {
                    failure.compareAndSet(null, t);
                    stop.set(true);
                } finally {
                    doneLatch.countDown();
                }
            }, "producer-" + producerId);
            producer.start();
        }

        for (int consumerId = 0; consumerId < consumerCount; consumerId++) {
            Thread consumer = new Thread(() -> {
                try {
                    startLatch.await();

                    while (!stop.get()) {
                        if (consumed.get() >= totalItems) {
                            break;
                        }

                        Object result = buffer.poll();
                        if (result == null) {
                            Thread.onSpinWait();
                            continue;
                        }

                        int value = (int) result;
                        int previous = seen.getAndIncrement(value);
                        if (previous != 0) {
                            failure.compareAndSet(null, new AssertionError("Duplicate item: " + value));
                            stop.set(true);
                            break;
                        }

                        consumed.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    failure.compareAndSet(null, e);
                    stop.set(true);
                } catch (Throwable t) {
                    failure.compareAndSet(null, t);
                    stop.set(true);
                } finally {
                    doneLatch.countDown();
                }
            }, "consumer-" + consumerId);
            consumer.start();
        }

        startLatch.countDown();

        boolean finished = doneLatch.await(20, TimeUnit.SECONDS);
        assertTrue(finished, "Test timed out");
        assertNull(failure.get(), "Failure in concurrent execution");

        assertEquals(totalItems, consumed.get(), "Not all items were consumed");

        for (int i = 0; i < totalItems; i++) {
            assertEquals(1, seen.get(i), "Item lost or duplicated: " + i);
        }
    }

    @AllArgsConstructor
    class ProducerRunnable implements Runnable {
        private int fromIncl;
        private int toExcl;
        private CountDownLatch startLatch;
        private CountDownLatch doneLatch;
        BoundedQueueBuffer buffer;
        AtomicBoolean stop;

        public void run() {
            try {
                startLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            int i = fromIncl;
            while (i < toExcl) {
                if (stop.get()) {
                    break;
                }
                if (buffer.offer(i)) {
                    i++;
                }
            }

            doneLatch.countDown();
        }
    }
}