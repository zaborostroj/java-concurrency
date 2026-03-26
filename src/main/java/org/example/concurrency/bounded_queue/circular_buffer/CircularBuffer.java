package org.example.concurrency.bounded_queue.circular_buffer;

import org.example.concurrency.bounded_queue.BoundedQueueBuffer;

public class CircularBuffer implements BoundedQueueBuffer {
    private Object[] items;
    private int head;
    private int tail;
    private int count;
    private final int capacity;

    public CircularBuffer(int capacity) {
        if (capacity <= 1) {
            throw new IllegalArgumentException("Capacity should be greater than 1");
        }
        this.capacity = capacity;
        this.items = new Object[capacity];
        this.head = 0;
        this.tail = 0;
        this.count = 0;
    }

    @Override
    public void produce(Object item) {
        synchronized (this) {
            while (count == capacity) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            items[tail] = item;
            tail = (tail + 1) % capacity;
            count++;
            notifyAll();
        }
    }

    @Override
    public Object consume() {
        synchronized (this) {
            while (count == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }

            var result = items[head];
            head = (head + 1) % capacity;
            count--;
            notifyAll();
            return result;
        }
    }
}
