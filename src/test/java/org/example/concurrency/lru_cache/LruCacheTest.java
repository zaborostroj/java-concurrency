package org.example.concurrency.lru_cache;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class LruCacheTest {
    @Test
    public void testGetMovesToHead() {
        int capacity = 10;
        LruCache<UUID, String> cache = new LruCache<>(capacity);

        List<UUID> uuids = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            var uuid = UUID.randomUUID();
            cache.put(uuid, i + " value");
            uuids.add(uuid);
        }

        String retrievedValue = cache.get(uuids.getFirst());
        assertEquals(0 + " value", retrievedValue, "Should return correct value");

        cache.put(UUID.randomUUID(), "new value");
        assertNotNull(cache.get(uuids.getFirst()));
        assertNull(cache.get(uuids.get(1)));
    }

    @Test
    public void testSizeConsistency() {
        int capacity = 3;
        LruCache<UUID, String> cache = new LruCache<>(capacity);

        List<UUID> uuids = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            var uuid = UUID.randomUUID();
            cache.put(uuid, i + " value");
            uuids.add(uuid);
        }

        assertEquals(capacity, cache.getSize(), "Size should be " + capacity + " after " + capacity + " put()");

        cache.put(UUID.randomUUID(), "new value");
        assertEquals(capacity, cache.getSize(), "Size should be " + capacity + " (eviction happened)");

        cache.get(uuids.getFirst());
        cache.get(uuids.getLast());
        assertEquals(capacity, cache.getSize(), "Size shouldn't change after get()");

        cache.remove(uuids.get(1));
        assertEquals(capacity - 1, cache.getSize(), "Size should be " + (capacity - 1) + " after remove()");

        cache.put(UUID.randomUUID(), "new value");
        assertEquals(capacity, cache.getSize(), "Size should be " + capacity + " after the last pub()");
    }

    @Test
    public void testConcurrentPutGet() throws InterruptedException {
        int capacity = 100;
        LruCache<String, Integer> cache = new LruCache<>(capacity);

        int operationsPerThread = 1000;
        int threadCount = 5;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(threadCount * 2);
        ConcurrentLinkedQueue<String> errors = new ConcurrentLinkedQueue<>();

        Runnable putTask = () -> {
            try {
                start.await();

                for (int i = 0; i < operationsPerThread; i++) {
                    cache.put("Thread_" + i, i);
                }
            } catch (Exception e) {
                errors.add("PUT error: " + e.getMessage());
            } finally {
                finish.countDown();
            }
        };

        Runnable getTask = () -> {
            try {
                start.await();

                for (int i = 0; i < operationsPerThread; i++) {
                    String key = "Thread_" + (i % threadCount);
                    Integer value = cache.get(key);

                    if (value != null && value < 0) {
                        errors.add("GET returned negative value: " + value);
                    }
                }
            } catch (Exception e) {
                errors.add("GET error: " + e.getMessage());
            } finally {
                finish.countDown();
            }
        };

        Thread[] putThreads = new Thread[threadCount];
        Thread[] getThreads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            putThreads[i] = new Thread(putTask, "PutThread-" + i);
            getThreads[i] = new Thread(getTask, "GetThread-" + i);
            putThreads[i].start();
            getThreads[i].start();
        }

        start.countDown();

        boolean completed = finish.await(30, TimeUnit.SECONDS);

        assertTrue(completed, "All threads should complete within 30 seconds");
        assertTrue(errors.isEmpty(), "Should have no errors: " + errors);
        int finalSize = cache.getSize();
        assertTrue(finalSize <= capacity, "Cache size should not exceed capacity. Size: " + finalSize + ", Capacity: " + capacity);
    }
}
