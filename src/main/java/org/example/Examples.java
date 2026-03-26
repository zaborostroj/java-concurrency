package org.example;

import org.example.concurrency.bounded_queue.BoundedQueuesDemo;
import org.example.concurrency.producer_consumer.ProducerConsumerDemo;
import org.example.concurrency.singleton.SingletonsDemo;

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
        ProducerConsumerDemo.runSempahore();
        ProducerConsumerDemo.runVolatileSpinWaiting();

        System.out.println("\n=== Bounded queues examples ===\n");
        BoundedQueuesDemo.circularBufferDemo();
    }
}
