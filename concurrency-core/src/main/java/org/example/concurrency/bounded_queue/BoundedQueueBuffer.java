package org.example.concurrency.bounded_queue;

public interface BoundedQueueBuffer {
    void produce(Object o);
    Object consume();

    default boolean offer(Object item) {
        throw new RuntimeException("Not implemented in this realisation");
    }

    default Object poll() {
        throw new RuntimeException("Not implemented in this realisation");
    }
}
