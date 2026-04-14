package org.example.concurrency.thread_pool;

import java.util.concurrent.Callable;

public class ExampleExecutorService<T> {
    private final BoundedQueue taskQueue;
    private final Thread[] workers;

    public ExampleExecutorService(int threadsNumber, int queueCapacity) {
        if (threadsNumber < 1) {
            throw new IllegalArgumentException("Threads number must be greater than 0");
        }

        if (queueCapacity < 1) {
            throw new IllegalArgumentException("Queue capacity must be greater than 0");
        }

        this.taskQueue = new BoundedQueue(queueCapacity);
        this.workers = new Thread[threadsNumber];
        for (int i = 0; i < threadsNumber; i++) {
            workers[i] = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    Runnable r;
                    try {
                        r = taskQueue.get();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    r.run();
                }
            }, "ExampleExecutorService-worker-" + i);
            workers[i].start();
        }
    }

    public ExampleFuture<T> submit(Callable<T> callable) throws InterruptedException {
        if (callable == null) {
            throw new IllegalArgumentException("Callable mustn't be null");
        }

        ExampleFuture<T> future = new ExampleFuture<>();

        Runnable wrapper = () -> {
            try {
                T result = callable.call();
                future.completeWithResult(result);
            } catch (Exception e) {
                future.completeWithException(e);
            }
        };

        taskQueue.put(wrapper);
        return future;
    }
}
