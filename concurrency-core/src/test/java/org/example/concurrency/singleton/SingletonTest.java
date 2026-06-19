package org.example.concurrency.singleton;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingletonTest {

    @Test
    void eagerSingletonIsSingleInstanceAcrossThreads() {
        int tasks = 100;

        Set<Integer> instanceIds = ConcurrentHashMap.newKeySet();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(tasks);

        for (int i = 0; i < tasks; i++) {
            Thread t = new Thread(() -> {
                try {
                    start.await();
                    instanceIds.add(System.identityHashCode(EagerSingleton.getInstance()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    end.countDown();
                }
            });
            t.start();
        }

        start.countDown();

        try {
            end.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertEquals(1, instanceIds.size());
    }

    @Test
    void lazySingletonIsSingleInstanceAcrossThreads() {
        int tasks = 100;

        Set<Integer> instanceIds = ConcurrentHashMap.newKeySet();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(tasks);

        for (int i = 0; i < tasks; i++) {
            Thread t = new Thread(() -> {
                try {
                    start.await();
                    instanceIds.add(System.identityHashCode(LazyInitSingleton.getInstance()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    end.countDown();
                }
            });
            t.start();
        }

        start.countDown();

        try {
            end.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertEquals(1, instanceIds.size());
    }

    @Test
    void doubleCheckLockingSingletonIsSingleInstanceAcrossThreads() {
        int tasks = 100;

        Set<Integer> instanceIds = ConcurrentHashMap.newKeySet();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(tasks);

        for (int i = 0; i < tasks; i++) {
            Thread t = new Thread(() -> {
                try {
                    start.await();
                    instanceIds.add(System.identityHashCode(DoubleCheckLockingSingleton.getInstance()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    end.countDown();
                }
            });
            t.start();
        }

        start.countDown();

        try {
            end.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertEquals(1, instanceIds.size());
    }

    @Test
    void initOnDemandHolderSingletonIsSingleInstanceAcrossThreads() {
        int tasks = 100;

        Set<Integer> instanceIds = ConcurrentHashMap.newKeySet();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(tasks);

        for (int i = 0; i < tasks; i++) {
            Thread t = new Thread(() -> {
                try {
                    start.await();
                    instanceIds.add(System.identityHashCode(InitOnDemandHolderSingleton.getInstance()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    end.countDown();
                }
            });
            t.start();
        }

        start.countDown();

        try {
            end.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertEquals(1, instanceIds.size());
    }

    @Test
    void enumSingletonIsSingleInstanceAcrossThreads() {
        int tasks = 100;

        Set<Integer> instanceIds = ConcurrentHashMap.newKeySet();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(tasks);

        for (int i = 0; i < tasks; i++) {
            Thread t = new Thread(() -> {
                try {
                    start.await();
                    instanceIds.add(System.identityHashCode(EnumSingleton.getInstance()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    end.countDown();
                }
            });
            t.start();
        }

        start.countDown();

        try {
            end.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertEquals(1, instanceIds.size());
    }
}
