package org.example.concurrency.thread_safe_counter;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerCounter {
    private AtomicInteger value;

    public AtomicIntegerCounter(int initialValue) {
        this.value = new AtomicInteger(initialValue);
    }

    public void increment() {
        value.incrementAndGet();
    }

    public void decrement() {
        value.decrementAndGet();
    }

    public int get() {
        return value.get();
    }
}
