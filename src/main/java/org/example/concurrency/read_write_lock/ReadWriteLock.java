package org.example.concurrency.read_write_lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReadWriteLock {
    private int activeReaders;
    private boolean isActiveWriterPresent;
    private int waitingWriters;
    private final ReentrantLock mutex;
    private final Condition canRead;
    private final Condition canWrite;

    public ReadWriteLock() {
        this.activeReaders = 0;
        this.isActiveWriterPresent = false;
        this.waitingWriters = 0;
        this.mutex = new ReentrantLock();
        this.canRead = mutex.newCondition();
        this.canWrite = mutex.newCondition();
    }

    public void lockRead() {
        try {
            mutex.lockInterruptibly();
            while (isActiveWriterPresent || waitingWriters > 0) {
                canRead.await();
            }
            activeReaders++;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (mutex.isHeldByCurrentThread()) {
                mutex.unlock();
            }
        }
    }

    public void unlockRead() {
        try {
            mutex.lockInterruptibly();
            activeReaders--;
            if (activeReaders == 0) {
                canWrite.signalAll();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (mutex.isHeldByCurrentThread()) {
                mutex.unlock();
            }
        }
    }

    public void lockWrite() {
        boolean lockingConditionsAreMet = false;

        try {
            mutex.lockInterruptibly();
            waitingWriters++;

            while (isActiveWriterPresent || activeReaders > 0) {
                canWrite.await();
            }
            lockingConditionsAreMet = true;

            isActiveWriterPresent = true;
            waitingWriters--;
        } catch (InterruptedException e) {
            if (!lockingConditionsAreMet) {
                waitingWriters--;
            }
            Thread.currentThread().interrupt();
        } finally {
            if (mutex.isHeldByCurrentThread()) {
                mutex.unlock();
            }
        }
    }

    public void unlockWrite() {
        try {
            mutex.lockInterruptibly();
            isActiveWriterPresent = false;
            if (waitingWriters > 0) {
                canWrite.signalAll();
            } else {
                canRead.signalAll();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (mutex.isHeldByCurrentThread()) {
                mutex.unlock();
            }
        }
    }
}
