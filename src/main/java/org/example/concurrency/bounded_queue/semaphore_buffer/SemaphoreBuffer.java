package org.example.concurrency.bounded_queue.semaphore_buffer;

import org.example.concurrency.bounded_queue.BoundedQueueBuffer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Semaphore;

public class SemaphoreBuffer implements BoundedQueueBuffer {
    private final Deque<Object> queue;
    private final int capacity;
    private final Semaphore mutex;
    private final Semaphore items;
    private final Semaphore slots;

    public SemaphoreBuffer(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.queue = new ArrayDeque<>();
        this.capacity = capacity;
        this.mutex = new Semaphore(1);
        this.items = new Semaphore(0);
        this.slots = new Semaphore(capacity);
    }

    @Override
    public void produce(Object item) {
        try {
            slots.acquire();
            mutex.acquire();
            try {
                queue.addLast(item);
            } finally {
                mutex.release();
            }
            items.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Object consume() {
        try {
            items.acquire();
            mutex.acquire();
            try {
                return queue.removeFirst();
            } finally {
                mutex.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            slots.release();
        }
    }
}
