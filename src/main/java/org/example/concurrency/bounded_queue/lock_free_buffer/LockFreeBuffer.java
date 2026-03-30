package org.example.concurrency.bounded_queue.lock_free_buffer;

import org.example.concurrency.bounded_queue.BoundedQueueBuffer;

import java.util.concurrent.atomic.AtomicLong;

public class LockFreeBuffer implements BoundedQueueBuffer {
    private final Cell[] items;
    private final AtomicLong writeIndex;
    private final AtomicLong readIndex;
    private final int capacity;

    public LockFreeBuffer(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity mustn't be lesser than 1");
        }
        this.capacity = capacity;
        this.writeIndex = new AtomicLong(0);
        this.readIndex = new AtomicLong(0);
        this.items = new Cell[capacity];
        for (int i = 0; i < capacity; i++) {
            // initialize sequence with distinct "producer-ready" state: 2 * i
            this.items[i] = new Cell(2L * i);
        }
    }

    @Override
    public boolean offer(Object item) {
        if (item == null) {
            throw new IllegalArgumentException("Item mustn't be null");
        }
        while (true) {
            long currentWriteIndex = this.writeIndex.get();
            int cellIndex = (int) (currentWriteIndex % capacity);
            Cell currentCell = this.items[cellIndex];

            long targetSequence = currentWriteIndex * 2L; // producer-ready state
            if (currentCell.sequence == targetSequence) {
                if (writeIndex.compareAndSet(currentWriteIndex, currentWriteIndex + 1)) {
                    currentCell.value = item;
                    // move to consumer-ready state
                    currentCell.sequence = targetSequence + 1;
                    return true;
                }
                // some other thread has written to this cell, move to another iteration
            } else if (currentCell.sequence < targetSequence) {
                // buffer is full
                return false;
            }
            Thread.onSpinWait();
        }
    }

    @Override
    public Object poll() {
        while (true) {
            long currentReadIndex = this.readIndex.get();
            int cellIndex = (int) (currentReadIndex % capacity);
            Cell currentCell = this.items[cellIndex];

            long targetSequence = currentReadIndex * 2L + 1; // consumer-ready state
            if (currentCell.sequence == targetSequence) {
                if (readIndex.compareAndSet(currentReadIndex, currentReadIndex + 1)) {
                    Object result = currentCell.value;
                    // move to next producer-ready state for this slot
                    currentCell.sequence = 2L * (currentReadIndex + this.capacity);
                    return result;
                }
                // some other thread has read this cell, move to another iteration
            } else if (currentCell.sequence < targetSequence) {
                // buffer is empty
                return null;
            }
            Thread.onSpinWait();
        }
    }

    @Override
    public Object consume() {
        throw new RuntimeException("Not implemented in this realisation");
    }

    @Override
    public void produce(Object item) {
        throw new RuntimeException("Not implemented in this realisation");
    }

    private static class Cell {
        volatile Object value;
        volatile long sequence;

        Cell(long initialSequenceValue) {
            this.sequence = initialSequenceValue;
        }
    }
}
