package org.example;

import org.example.concurrency.singleton.SingletonsDemo;
import org.example.producer_consumer.ProducerConsumerDemo;

public class Examples {
    private Examples() {}

    public static void runAll() {
        System.out.println("\n=== Singleton examples ===\n");
        SingletonsDemo.runEagerSingleton();
        SingletonsDemo.runLazyInitSingleton();
        SingletonsDemo.runDoubleCheckLockingSingleton();
        SingletonsDemo.runOnDemandHolderSingleton();
        SingletonsDemo.runEnumSingleton();

        System.out.println("\n=== Producers-consumers examples ===\n");
        ProducerConsumerDemo.runWaitNotify();
        ProducerConsumerDemo.runLockWithCondition();
    }
}
