package org.example.concurrency.thread_pool;

public class ExampleFuture<T> {
    private T result;
    private Exception exception;
    private ExampleFutureState state;

    public ExampleFuture() {
        this.state = ExampleFutureState.PENDING;
        this.result = null;
        this.exception = null;
    }

    public T get() {
        synchronized (this) {
            while (state != ExampleFutureState.DONE) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
            if (exception != null) {
                throw new RuntimeException(exception);
            }
            return result;
        }
    }

    void completeWithResult(T result) {
        synchronized (this) {
            if (state == ExampleFutureState.DONE) {
                return;
            }
            this.result = result;
            this.exception = null;
            this.state = ExampleFutureState.DONE;
            notifyAll();
        }
    }

    void completeWithException(Exception exception) {
        synchronized (this) {
            if (state == ExampleFutureState.DONE) {
                return;
            }
            this.result = null;
            this.exception = exception;
            this.state = ExampleFutureState.DONE;
            notifyAll();
        }
    }

    public static enum ExampleFutureState {
        PENDING, DONE
    }
}
