package org.example.concurrency.thread_safe_counter;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantLockCounter {
    private int value;
    private final ReadWriteLock mutex;

    public ReentrantLockCounter(int initialValue) {
        this.value = initialValue;
        this.mutex = new ReentrantReadWriteLock();
    }

    public void increment() {
        try {
            this.mutex.writeLock().lock();
            this.value++;
        } finally {
            this.mutex.writeLock().unlock();
        }
    }

    public void decrement() {
        try {
            this.mutex.writeLock().lock();
            this.value--;
        } finally {
            this.mutex.writeLock().unlock();
        }
    }

    public int get() {
        try {
            this.mutex.readLock().lock();
            return this.value;
        } finally {
            this.mutex.readLock().unlock();
        }
    }
}
