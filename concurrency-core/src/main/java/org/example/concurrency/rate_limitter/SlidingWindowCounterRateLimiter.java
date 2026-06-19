package org.example.concurrency.rate_limitter;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowCounterRateLimiter {
    private int previousWindowCount;
    private int currentWindowCount;

    private final Duration windowSize;
    private final int requestsPerWindow;
    Instant windowStartTime;

    ReentrantLock mutex;

    public SlidingWindowCounterRateLimiter(
        int windowSizeSeconds,
        int requestsPerWindow
    ) {
        this.previousWindowCount = 0;
        this.currentWindowCount = 0;
        this.windowSize = Duration.ofSeconds(windowSizeSeconds);
        this.requestsPerWindow = requestsPerWindow;
        windowStartTime = Instant.now();
        mutex = new ReentrantLock();
    }

    public LimiterResponse doFilter(LimiterRequest request) {
        try {
            mutex.lockInterruptibly();

            Instant now = Instant.now();
            if (now.isAfter(windowStartTime.plus(windowSize))) {
                long passedWindowsCount = Duration.between(windowStartTime, now).toNanos() / windowSize.toNanos();
                windowStartTime = windowStartTime.plus(passedWindowsCount * windowSize.toNanos(), ChronoUnit.NANOS);

                previousWindowCount = currentWindowCount;
                currentWindowCount = 0;
            }

            Duration timePassedInCurrentWindow = Duration.between(windowStartTime, now);
            Duration timeRemainingInCurrentWindow = windowSize.minus(timePassedInCurrentWindow);

            if (timeRemainingInCurrentWindow.isNegative()) {
                timeRemainingInCurrentWindow = Duration.ZERO;
            }

            long timeRemainingNanos = timeRemainingInCurrentWindow.toNanos();
            long windowSizeNanos = windowSize.toNanos();

            double requestsFromPreviousWindow = (double)previousWindowCount *
                    (timeRemainingNanos / (double)windowSizeNanos);

            double totalRequests = currentWindowCount + requestsFromPreviousWindow;

            if (totalRequests < requestsPerWindow) {
                currentWindowCount++;
                return new LimiterResponse(LimiterResponseStatus.ACCEPTED,
                        String.format("Request #%d accepted. Current window: %d, Total (with previous): %.2f",
                                request.getId(), currentWindowCount, totalRequests));
            } else {
                return new LimiterResponse(LimiterResponseStatus.REJECTED,
                        String.format("Request #%d rejected. Limit exceeded: %.2f / %d",
                                request.getId(), totalRequests, requestsPerWindow));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new LimiterResponse(LimiterResponseStatus.ERROR, "Current thread was interrupted");
        } finally {
            mutex.unlock();
        }
    }
}
