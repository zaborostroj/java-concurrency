package org.example.concurrency.bounded_queue.reentrant_lock_buffer;

import org.example.concurrency.bounded_queue.BoundedQueueBuffer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockBuffer implements BoundedQueueBuffer {
    private final Object[] items;
    private int head;
    private int tail;
    private int count;
    private final int capacity;
    private final Lock lock;
    private final Condition notEmpty;
    private final Condition notFull;

    public ReentrantLockBuffer(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.items = new Object[capacity];
        this.capacity = capacity;
        this.head = 0;
        this.tail = 0;
        this.count = 0;
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
    }

    @Override
    public void produce(Object item) {
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        try {
            while (count == capacity) {
                System.out.println("Producer " + Thread.currentThread().getName() + " is waiting");
                notFull.await();
            }
            items[tail] = item;
            tail = (tail + 1) % capacity;
            count++;
            notEmpty.signalAll();
            System.out.println("Producer " + Thread.currentThread().getName() + " added item to the queue " + item);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object consume() {
        try {
            System.out.println("Consumer " + Thread.currentThread().getName() + " is waiting");
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }

        try {
            while (count == 0) {
                notEmpty.await();
            }
            var result = items[head];
            head = (head + 1) % capacity;
            count--;
            notFull.signalAll();
            System.out.println("Consumer " + Thread.currentThread().getName() + " consumed item from the queue " + result);
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            lock.unlock();
        }
    }
}
