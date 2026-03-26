package org.example.concurrency.bounded_queue;

public interface BoundedQueueBuffer {
    void produce(Object o);
    Object consume();
}
