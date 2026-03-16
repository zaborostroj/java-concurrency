package org.example.concurrency.singleton;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

public class SingletonsDemo {
    private SingletonsDemo() {}

    public static void runEagerSingleton() {
        run("Eager singleton demo", EagerSingleton::getInstance);
    }

    public static void runLazyInitSingleton() {
        run("Lazy init singleton demo", LazyInitSingleton::getInstance);
    }

    public static void runDoubleCheckLockingSingleton() {
        run("Double check locking singleton demo", DoubleCheckLockingSingleton::getInstance);
    }

    public static void runOnDemandHolderSingleton() {
        run("On demand holder singleton demo", InitOnDemandHolderSingleton::getInstance);
    }

    public static void runEnumSingleton() {
        run("Enum singleton demo", EnumSingleton::getInstance);
    }

    private static void run(String title, Supplier<?> singletonSupplier) {
        System.out.println(title);

        int threads = Math.max(4, Runtime.getRuntime().availableProcessors());
        int tasks = 100;

        Set<Integer> instanceIds = ConcurrentHashMap.newKeySet();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(threads);

        for (int i = 0; i < tasks; i++) {
            Thread t = new Thread(() -> {
                try {
                    start.await();
                    Object instance = singletonSupplier.get();
                    instanceIds.add(System.identityHashCode(instance));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    end.countDown();
                }
            }, "worker-" + i);
            t.start();
        }

        start.countDown();

        try {
            end.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Tasks: " + tasks);
        System.out.println("Unique singleton instances found: " + instanceIds.size());

        try {
            Object instance = singletonSupplier.get();
            String message = (String) instance.getClass().getMethod("sayHello").invoke(instance);
            System.out.println("Example call: " + message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
