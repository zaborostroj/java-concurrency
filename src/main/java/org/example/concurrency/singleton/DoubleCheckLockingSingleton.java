package org.example.concurrency.singleton;

public class DoubleCheckLockingSingleton {
    /**
     * Without volatile, you risk encountering the instruction reordering problem by the compiler or processor.
     * The process of creating an object new DoubleCheckLockingSingleton() consists of three steps:
     * Memory allocation.
     * Object initialization (constructor call).
     * Writing the reference to the INSTANCE variable.
     * The JVM can reorder steps 2 and 3. In this case:
     * Thread A enters the synchronized block and assigns the memory address to the INSTANCE variable (step 3), but the constructor has not yet completed (step 2).
     * Thread B calls getInstance(), sees that INSTANCE != null (first check), and immediately returns the object.
     * Thread B starts working with an object that is not yet fully initialized, which will lead to hard-to-catch bugs.
     * Using volatile guarantees the "happens-before" rule: writing to the variable will be visible to all threads only after all previous actions are completed (including the constructor's work).
     */
    private static volatile DoubleCheckLockingSingleton INSTANCE;

    private DoubleCheckLockingSingleton() {}

    /**
     * Micro-optimization:
     * use local variable to avoid repeated volatile variable reads
     */
    public static DoubleCheckLockingSingleton getInstance() {
        DoubleCheckLockingSingleton instance = INSTANCE;
        if (instance == null) {
            synchronized (DoubleCheckLockingSingleton.class) {
                instance = INSTANCE;
                if (instance == null) {
                    INSTANCE = new DoubleCheckLockingSingleton();
                    instance = INSTANCE;
                }
            }
        }
        return instance;
    }

    public String sayHello() {
        return "Hello from " + getClass().getSimpleName() + "@" + System.identityHashCode(this);
    }
}
