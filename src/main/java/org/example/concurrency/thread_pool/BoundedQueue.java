package org.example.concurrency.thread_pool;

public class BoundedQueue {
    private final Runnable[] tasks;
    private int head;
    private int tail;
    private int size;

    public BoundedQueue(int capacity) {
        this.tasks = new Runnable[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    public synchronized void put(Runnable task) throws InterruptedException {
        while (size == tasks.length) {
            wait();
        }
        tasks[tail] = task;
        tail = (tail + 1) % tasks.length;
        size++;
        notifyAll();
    }

    public synchronized Runnable get() throws InterruptedException {
        while (size == 0) {
            wait();
        }
        Runnable result = tasks[head];
        tasks[head] = null;
        head = (head + 1) % tasks.length;
        size--;
        notifyAll();
        return result;
    }
}
