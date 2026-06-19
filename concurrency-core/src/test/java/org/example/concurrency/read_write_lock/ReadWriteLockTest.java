package org.example.concurrency.read_write_lock;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadWriteLockTest {
    private final String readerLog = "READER_ENTER";
    private final String writerLog = "WRITER_ENTER";

    @Test
    void testWriterPreference() throws InterruptedException {
        int readersCount = 3;

        ReadWriteLock lock = new ReadWriteLock();
        ConcurrentLinkedQueue<String> lockingQueue = new ConcurrentLinkedQueue<>();

        CountDownLatch firstReadersEntered = new CountDownLatch(readersCount);
        CountDownLatch allowFirstReadersToExit = new CountDownLatch(1);

        // Writer signals that it has actually entered the writing-critical section
        CountDownLatch writerEntered = new CountDownLatch(1);
        CountDownLatch allowWriterToExit = new CountDownLatch(1);

        // If writer-preference is respected, second readers should NOT enter
        // (i.e., lockRead() should block) while writer is active.
        CountDownLatch secondReadersEntered = new CountDownLatch(readersCount);

        List<Thread> threads = new ArrayList<>();

        // 1) Start first group readers: they enter and then wait until we release them.
        for (int i = 0; i < readersCount; i++) {
            Thread t = new Thread(() -> {
                lock.lockRead();
                try {
                    lockingQueue.add(readerLog);
                    firstReadersEntered.countDown();
                    allowFirstReadersToExit.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlockRead();
                }
            });
            threads.add(t);
            t.start();
        }

        assertTrue(firstReadersEntered.await(2, TimeUnit.SECONDS), "First readers should enter");

        // 2) Start writer: it must wait for first readers to exit.
        Thread writer = new Thread(() -> {
            lock.lockWrite();
            try {
                lockingQueue.add(writerLog);
                writerEntered.countDown();
                // Hold writer while we verify that second readers didn't pass.
                allowWriterToExit.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlockWrite();
            }
        });
        threads.add(writer);
        writer.start();

        // 3) Start second group readers while the writer is waiting.
        // They must NOT be allowed to enter when waiting while the writer exists (writer-preference).
        for (int i = 0; i < readersCount; i++) {
            Thread t = new Thread(() -> {
                lock.lockRead();
                try {
                    lockingQueue.add(readerLog);
                    secondReadersEntered.countDown();
                } finally {
                    lock.unlockRead();
                }
            });
            threads.add(t);
            t.start();
        }

        // Release first readers so that writer can proceed.
        allowFirstReadersToExit.countDown();

        // Wait until writer is ACTIVE.
        assertTrue(writerEntered.await(2, TimeUnit.SECONDS), "Writer should enter write critical section");

        // While writer is active, second readers must NOT be able to enter.
        assertFalse(secondReadersEntered.await(300, TimeUnit.MILLISECONDS),
                "Second readers must not enter before writer exits (writer-preference)");

        // Let writer exit.
        allowWriterToExit.countDown();

        // Now second readers should be able to enter.
        assertTrue(secondReadersEntered.await(2, TimeUnit.SECONDS), "Second readers should enter after writer exits");

        // Join all threads to avoid leaking background activity between tests.
        for (Thread t : threads) {
            t.join(2000);
            assertFalse(t.isAlive(), "Thread should finish");
        }

        // Finally, assert the ordering in the shared log:
        // first group readers first (3), then writer, then second group readers (3).
        assertEquals(readerLog, lockingQueue.poll(), "First reader #1 should be first in queue");
        assertEquals(readerLog, lockingQueue.poll(), "First reader #2 should be second in queue");
        assertEquals(readerLog, lockingQueue.poll(), "First reader #3 should be third in queue");

        assertEquals(writerLog, lockingQueue.poll(), "Writer should be next in queue");

        assertEquals(readerLog, lockingQueue.poll(), "Second reader #1 should be after writer");
        assertEquals(readerLog, lockingQueue.poll(), "Second reader #2 should be after writer");
        assertEquals(readerLog, lockingQueue.poll(), "Second reader #3 should be after writer");

        assertNull(lockingQueue.poll(), "Queue should contain exactly 7 log entries");
    }

    @Test
    public void testReadersAreParallel() throws InterruptedException {
        int readersCount = 10;
        AtomicInteger enteredReaders = new AtomicInteger(0);
        AtomicInteger maxEnteredReaders = new AtomicInteger(0);
        ReadWriteLock lock = new ReadWriteLock();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch allEntered = new CountDownLatch(readersCount);
        CountDownLatch allowRelease = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>(readersCount);

        for (int i = 0; i < readersCount; i++) {
            Thread t = new Thread(() -> {
                try {
                    start.await();
                    lock.lockRead();

                    int current = enteredReaders.incrementAndGet();

                    int prev;
                    do {
                        prev = maxEnteredReaders.get();
                        if (current <= prev) {
                            break;
                        }
                    } while (!maxEnteredReaders.compareAndSet(prev, current));

                    allEntered.countDown();

                    if (current >= 2) {
                        allowRelease.countDown();
                    }

                    allowRelease.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlockRead();
                }
            });
            threads.add(t);
            t.start();
        }

        start.countDown();

        assertTrue(allEntered.await(2, TimeUnit.SECONDS));

        assertTrue(maxEnteredReaders.get() > 1, String.format("Only %d threads entered", maxEnteredReaders.get()));

        for (Thread t : threads) {
            t.join(2000);
            assertFalse(t.isAlive(), String.format("Thread %s is still alive", t.getName()));
        }
    }
}