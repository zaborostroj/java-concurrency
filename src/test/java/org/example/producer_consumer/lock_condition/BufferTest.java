package org.example.producer_consumer.lock_condition;

import org.example.concurrency.producer_consumer.lock_condition.Buffer;
import org.example.concurrency.producer_consumer.lock_condition.Message;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BufferTest {
    @Test
    public void testBufferIsFifo() {
        Buffer buffer = new Buffer(2);
        Message message1 = new Message("1");
        Message message2 = new Message("2");

        buffer.produce(message1);
        buffer.produce(message2);

        var result1 = buffer.consume();
        var result2 = buffer.consume();

        assertEquals(result1, message1);
        assertEquals(result2, message2);
    }

    @Test
    public void testBufferBlocksWhenFull() {
        Buffer buffer = new Buffer(2);
        Message message1 = new Message("1");
        Message message2 = new Message("2");
        Message message3 = new Message("3");

        buffer.produce(message1);
        buffer.produce(message2);

        Thread t = new Thread(() -> buffer.produce(message3));
        t.start();

        await().atMost(1, TimeUnit.SECONDS).until(() -> t.getState() == Thread.State.WAITING);

        t.interrupt();
        try {
            t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testBufferBlocksWhenEmpty() {
        Buffer buffer = new Buffer(2);
        Message message = new Message("1");

        Thread t = new Thread(buffer::consume);
        t.start();
        await().atMost(1, TimeUnit.SECONDS).until(() -> t.getState() == Thread.State.WAITING);

        buffer.produce(message);
        await().atMost(1, TimeUnit.SECONDS).until(() -> t.getState() == Thread.State.TERMINATED);

        t.interrupt();
        try {
            t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}