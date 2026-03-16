package org.example.concurrency.singleton;

public class LazyInitSingleton {
    private static LazyInitSingleton INSTANCE;

    private LazyInitSingleton() {}

    public static synchronized LazyInitSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LazyInitSingleton();
        }
        return INSTANCE;
    }

    public String sayHello() {
        return "Hello from " + getClass().getSimpleName() + "@" + System.identityHashCode(this);
    }
}
