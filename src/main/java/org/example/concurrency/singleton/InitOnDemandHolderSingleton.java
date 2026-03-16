package org.example.concurrency.singleton;

/**
 * Lazy Initialization: The outer class InitOnDemandHolderSingleton can be loaded by the JVM (for example, if you access
 * its other static methods or constants), but the inner class Holder will not be loaded until the getInstance()
 * method is called. Consequently, the INSTANCE object will be created only when actually needed.
 * Thread Safety: According to the Java specification (JLS), class initialization is thread-safe. The JVM guarantees
 * that static initializers are executed sequentially and atomically. If two threads simultaneously call getInstance(),
 * the JVM will ensure that Holder is initialized only once.
 * Performance: Unlike Double-Checked Locking, the volatile keyword is not needed here, and unlike a synchronized
 * method, there is no synchronized overhead on each call. This is one of the most efficient ways to implement
 * Singleton in Java.
 */
public class InitOnDemandHolderSingleton {
    private static class Holder {
        private static final InitOnDemandHolderSingleton INSTANCE = new InitOnDemandHolderSingleton();
    }

    private InitOnDemandHolderSingleton() {}

    public static InitOnDemandHolderSingleton getInstance() {
        return Holder.INSTANCE;
    }

    public String sayHello() {
        return "Hello from " + getClass().getSimpleName() + "@" + System.identityHashCode(this);
    }
}
