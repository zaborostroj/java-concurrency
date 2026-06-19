package org.example.concurrency.thread_safe_counter;

public class SynchronizedCounter {
    private long value;

    public SynchronizedCounter(long initialValue) {
        this.value = initialValue;
    }

    public synchronized void increment() {
        value++;
    }

    public synchronized void decrement() {
        value--;
    }

    public synchronized long get() {
        return value;
    }
}
