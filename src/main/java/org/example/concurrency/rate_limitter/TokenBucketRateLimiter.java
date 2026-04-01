package org.example.concurrency.rate_limitter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBucketRateLimiter {
    private final int capacity;
    private final int refillRatePerSecond;

    private int currentTokens;
    private Instant lastRefillTime;
    private final ReentrantLock lock;

    public TokenBucketRateLimiter(int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.currentTokens = capacity;
        this.lastRefillTime = Instant.now();
        this.lock = new ReentrantLock();
    }

    public LimiterResponse doFilter(LimiterRequest request) {
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new LimiterResponse(LimiterResponseStatus.ERROR, "Limiter thread is interrupted");
        }

        try {
            refreshTokens();

            if (currentTokens == 0) {
                return new LimiterResponse(LimiterResponseStatus.REJECTED, String.format("Request #%s rejected. No tokens left.", request.getId()));
            }

            currentTokens--;
            return new LimiterResponse(LimiterResponseStatus.ACCEPTED, String.format("Request #%s processed. %s of %s tokens left.", request.getId(), currentTokens, capacity));
        } finally {
            lock.unlock();
        }
    }

    private void refreshTokens() {
        if (currentTokens == capacity) {
            return;
        }

        long tokensToAdd = (lastRefillTime.until(Instant.now(), ChronoUnit.SECONDS)) * refillRatePerSecond;

        if (tokensToAdd == 0) {
            return;
        }

        if (tokensToAdd + currentTokens > capacity) {
            currentTokens = capacity;
        } else {
            currentTokens += (int) tokensToAdd;
        }
        lastRefillTime = Instant.now();
    }

    public int getCurrentTokens() {
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            refreshTokens();
            return this.currentTokens;
        } finally {
            lock.unlock();
        }
    }
}
