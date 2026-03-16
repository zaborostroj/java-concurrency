package org.example.concurrency.singleton;

public class EagerSingleton {
    private static final EagerSingleton INSTANCE = new EagerSingleton();

    private EagerSingleton() {}

    public static EagerSingleton getInstance() {
        return INSTANCE;
    }

    public String sayHello() {
        return "Hello from " + getClass().getSimpleName() + "@" + System.identityHashCode(this);
    }
}
