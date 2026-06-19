package org.example.concurrency.thread_safe_counter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtomicIntegerCounterTest {

    static Stream<Integer> threadsCount() {
        return Stream.of(2, 10, 100);
    }

    @ParameterizedTest(name = "threads={0}")
    @MethodSource("threadsCount")
    public void testMultipleThreadIncrement(int threads) throws InterruptedException {
        AtomicIntegerCounter counter = new AtomicIntegerCounter(0);

        var repeats = 100;
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(threads);

        try (ExecutorService executorService = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                executorService.submit(() -> {
                    try {
                        startSignal.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }

                    for (int j = 0; j < repeats; j++) {
                        counter.increment();
                    }

                    doneSignal.countDown();
                });
            }

            assertEquals(0, counter.get());
            startSignal.countDown();

            assertTrue(doneSignal.await(5, TimeUnit.SECONDS), "Timeout waiting for threads to complete");
            assertEquals((long) threads * repeats, counter.get());
        }
    }

    @ParameterizedTest(name = "threads={0}")
    @MethodSource("threadsCount")
    public void testMultipleThreadDecrement(int threads) throws InterruptedException {
        AtomicIntegerCounter counter = new AtomicIntegerCounter(0);

        var repeats = 100;
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(threads);

        try (ExecutorService executorService = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                executorService.submit(() -> {
                    try {
                        startSignal.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }

                    for (int j = 0; j < repeats; j++) {
                        counter.decrement();
                    }

                    doneSignal.countDown();
                });
            }

            assertEquals(0, counter.get());
            startSignal.countDown();

            assertTrue(doneSignal.await(5, TimeUnit.SECONDS), "Timeout waiting for threads to complete");
            assertEquals(-(long) threads * repeats, counter.get());
        }
    }

    @ParameterizedTest(name = "threads={0}")
    @MethodSource("threadsCount")
    public void testMultipleThreadIncrementAndDecrement(int threads) throws InterruptedException {
        AtomicIntegerCounter counter = new AtomicIntegerCounter(0);

        var repeats = 100;
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(threads);

        try (ExecutorService executorService = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                executorService.submit(() -> {
                    try {
                        startSignal.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }

                    for (int j = 0; j < repeats; j++) {
                        if (j % 2 == 0) {
                            counter.decrement();
                        } else {
                            counter.increment();
                        }
                    }

                    doneSignal.countDown();
                });
            }

            assertEquals(0, counter.get());
            startSignal.countDown();

            assertTrue(doneSignal.await(5, TimeUnit.SECONDS), "Timeout waiting for threads to complete");
            assertEquals(0, counter.get());
        }
    }
}