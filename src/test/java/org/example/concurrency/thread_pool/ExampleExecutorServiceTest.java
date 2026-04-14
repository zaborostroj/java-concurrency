package org.example.concurrency.thread_pool;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExampleExecutorServiceTest {
    @Test
    public void testSubmitReturnsResultInFutureGet() throws InterruptedException {
        String expectedResult = "42";
        ExampleExecutorService<String> executorService = new ExampleExecutorService<>(1, 1);

        ExampleFuture<String> future = executorService.submit(() -> expectedResult);

        assertEquals(expectedResult, future.get());
    }

    @Test
    public void testSubmitThrowsRuntimeException() throws InterruptedException {
        ExampleExecutorService<String> executorService = new ExampleExecutorService<>(1, 1);

        ExampleFuture<String> future = executorService.submit(() -> {
            throw new IllegalArgumentException("boom");
        });

        assertThrows(RuntimeException.class, future::get);
    }

    @Test
    public void testSubmitBlocksWhenQueueIsFull() throws InterruptedException {
        ExampleExecutorService<String> executorService = new ExampleExecutorService<>(1, 1);
        CountDownLatch firstTaskCanProceed = new CountDownLatch(1);
        CountDownLatch secondTaskCanProceed = new CountDownLatch(1);

        executorService.submit(() -> {
            firstTaskCanProceed.await();
            return "42";
        });

        executorService.submit(() -> {
            secondTaskCanProceed.await();
            return "43";
        });

        Thread blockedThread = new Thread(() -> {
            try {
                executorService.submit(() -> "44");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        blockedThread.start();


        await().atMost(1, TimeUnit.SECONDS).until(() -> blockedThread.getState() == Thread.State.WAITING);

        firstTaskCanProceed.countDown();
        secondTaskCanProceed.countDown();

        await().atMost(1, TimeUnit.SECONDS).until(() -> blockedThread.getState() == Thread.State.TERMINATED);
    }
}