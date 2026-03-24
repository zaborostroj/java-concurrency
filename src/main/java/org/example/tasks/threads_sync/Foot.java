package org.example.tasks.threads_sync;

import lombok.Getter;
import lombok.Setter;

/**
 * Дан класс Foot - нога робота. В Robot.main() запускаются два потока для правой и левой ног.
 * Ноги ходят в случайном порядке. Надо сделать шаги строго по очереди.
 */
//public class Foot implements Runnable {
//    private final String name;
//
//    public Foot(String name) {
//        this.name = name;
//    }
//
//    @Override
//    public void run() {
//        while (true) {
//            step();
//        }
//    }
//
//    public void step() {
//        System.out.println(this.name + " foot step");
//    }
//}
//
//class Robot {
//    static void main(String[] args) {
//        Thread leftFoot = new Thread(new Foot("Left"));
//        leftFoot.start();
//        Thread rightFoot = new Thread(new Foot("Right"));
//        rightFoot.start();
//    }
//}

public class Foot implements Runnable {
    private final String name;
    private final FeetLock mutex;

    public Foot(String name, FeetLock mutex) {
        this.name = name;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            step();
        }
    }

    public void step() {
        synchronized (this.mutex) {
            if (this.mutex.getLastThreadName() == null || !this.mutex.getLastThreadName().equals(this.name)) {
                this.mutex.setLastThreadName(this.name);
                System.out.println(this.name + " foot step");
            }
        }
    }
}

@Getter
@Setter
class FeetLock {
    private String lastThreadName;
}

class Robot {
    static void main(String[] args) {
        FeetLock mutex = new FeetLock();
        Thread leftFoot = new Thread(new Foot("Left", mutex));
        Thread rightFoot = new Thread(new Foot("Right", mutex));

        leftFoot.start();
        rightFoot.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            leftFoot.interrupt();
            rightFoot.interrupt();

            try {
                leftFoot.join();
                rightFoot.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
