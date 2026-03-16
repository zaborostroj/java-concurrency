package org.example.concurrency;

import org.example.concurrency.singleton.SingletonsDemo;

public class Examples {
    private Examples() {}

    public static void runAll() {
        System.out.println("Java Concurrency examples");
        SingletonsDemo.runEagerSingleton();
        SingletonsDemo.runLazyInitSingleton();
        SingletonsDemo.runDoubleCheckLockingSingleton();
        SingletonsDemo.runOnDemandHolderSingleton();
        SingletonsDemo.runEnumSingleton();
    }
}
