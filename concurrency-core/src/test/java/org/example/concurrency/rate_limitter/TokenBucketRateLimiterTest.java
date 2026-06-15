package org.example.concurrency.rate_limitter;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenBucketRateLimiterTest {
    @Test
    public void testFirstRequestShouldWork() {
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(1, 1);

        var response1 = rateLimiter.doFilter(new LimiterRequest(1, "1"));
        var response2 = rateLimiter.doFilter(new LimiterRequest(2, "2"));

        assertEquals(LimiterResponseStatus.ACCEPTED, response1.getStatus(), "1 response should have ACCEPTED status, but is REJECTED");
        assertEquals(LimiterResponseStatus.REJECTED, response2.getStatus(), "1 response should have REJECTED status, but is ACCEPTED");
    }

    @Test
    public void testTokensLimit() {
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(3, 1);
        LimiterRequest request = new LimiterRequest(1, "1");
        List<LimiterResponse> responses = new ArrayList<>(4);

        for (int i = 0; i < 4; i++) {
            responses.add(rateLimiter.doFilter(request));
        }

        assertEquals(LimiterResponseStatus.ACCEPTED, responses.getFirst().getStatus(), "1 response should have ACCEPTED status, but is REJECTED");
        assertEquals(LimiterResponseStatus.ACCEPTED, responses.get(1).getStatus(), "2 response should have ACCEPTED status, but is REJECTED");
        assertEquals(LimiterResponseStatus.ACCEPTED, responses.get(2).getStatus(), "3 response should have ACCEPTED status, but is REJECTED");
        assertEquals(LimiterResponseStatus.REJECTED, responses.getLast().getStatus(), "4 response should have REJECTED status, but is ACCEPTED");
    }

    @Test
    public void testTokensRefill() {
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(3, 1);
        LimiterRequest request = new LimiterRequest(1, "1");
        List<LimiterResponse> responses = new ArrayList<>(4);

        for (int i = 0; i < 4; i++) {
            responses.add(rateLimiter.doFilter(request));
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        responses.add(rateLimiter.doFilter(request));

        assertEquals(LimiterResponseStatus.ACCEPTED, responses.getFirst().getStatus(), "1 response should have ACCEPTED status, but is REJECTED");
        assertEquals(LimiterResponseStatus.ACCEPTED, responses.get(1).getStatus(), "2 response should have ACCEPTED status, but is REJECTED");
        assertEquals(LimiterResponseStatus.ACCEPTED, responses.get(2).getStatus(), "3 response should have ACCEPTED status, but is REJECTED");
        assertEquals(LimiterResponseStatus.REJECTED, responses.get(3).getStatus(), "4 response should have REJECTED status, but is ACCEPTED");
        assertEquals(LimiterResponseStatus.ACCEPTED, responses.getLast().getStatus(), "5 response should have ACCEPTED status, but is REJECTED");
    }

    @Test
    void testNoOverflowBeyondCapacity() throws InterruptedException {
        int capacity = 10;
        int refillRate = 2;
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(capacity, refillRate);

        for (int i = 0; i < capacity; i++) {
            limiter.doFilter(new LimiterRequest(i, ""));
        }
        assertEquals(0, limiter.getCurrentTokens());

        Thread.sleep(5000);

        int tokensAfterWait = limiter.getCurrentTokens();
        assertEquals(capacity, tokensAfterWait, "After 5 seconds, should have exactly " + capacity + " tokens, got " + tokensAfterWait);
        assertTrue(tokensAfterWait <= capacity, "Tokens should not exceed capacity");
    }

    @Test
    void testConcurrentRequestsWithCapacityLimit() throws InterruptedException {
        int capacity = 5;
        int refillRate = 1;
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(capacity, refillRate);

        AtomicInteger acceptedCount = new AtomicInteger(0);
        AtomicInteger rejectedCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(10);

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    LimiterResponse response = limiter.doFilter(new LimiterRequest(threadId, "concurrent-request"));
                    if (response.getStatus() == LimiterResponseStatus.ACCEPTED) {
                        acceptedCount.incrementAndGet();
                    } else if (response.getStatus() == LimiterResponseStatus.REJECTED) {
                        rejectedCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        executor.shutdown();

        assertEquals(capacity, acceptedCount.get(), "Expected exactly " + capacity + " requests to be accepted");
        assertEquals(10 - capacity, rejectedCount.get(), "Expected " + (10 - capacity) + " requests to be rejected");
    }

    @Test
    void testBoundaryRequestsAtSecondBoundary() throws InterruptedException {
        int capacity = 10;
        int refillRate = 5;
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(capacity, refillRate);

        for (int i = 0; i < capacity; i++) {
            limiter.doFilter(new LimiterRequest(i, ""));
        }

        assertEquals(0, limiter.getCurrentTokens());

        Thread.sleep(500);

        int tokensAt500ms = limiter.getCurrentTokens();
        assertEquals(0, tokensAt500ms, "At 500ms, no new tokens should be added due to second granularity");

        Thread.sleep(600);

        int tokensAt1100ms = limiter.getCurrentTokens();
        assertEquals(refillRate, tokensAt1100ms, "At 1.1 seconds, " + refillRate + " tokens should be added");
    }

    @Test
    void testInterruptExceptionHandling() throws InterruptedException {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(5, 1);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger responsesReceived = new AtomicInteger(0);

        Thread testThread = new Thread(() -> {
            LimiterResponse response = limiter.doFilter(new LimiterRequest(1, ""));
            if (response.getStatus() == LimiterResponseStatus.ERROR) {
                responsesReceived.incrementAndGet();
            }
            latch.countDown();
        });

        testThread.start();
        testThread.interrupt();

        assertTrue(latch.await(3, TimeUnit.SECONDS), "Thread should handle interrupt and return");
        assertTrue(Thread.interrupted() || responsesReceived.get() == 1, "Interrupt should be handled gracefully");
    }

    @Test
    void testRecoveryAfterLongIdleTime() throws InterruptedException {
        int capacity = 10;
        int refillRate = 2;
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(capacity, refillRate);

        limiter.doFilter(new LimiterRequest(1, ""));
        assertEquals(capacity - 1, limiter.getCurrentTokens());

        Thread.sleep(5100);

        int tokensAfterIdle = limiter.getCurrentTokens();
        assertEquals(capacity, tokensAfterIdle, "After long idle time, tokens should be restored to capacity");

        for (int i = 0; i < 5; i++) {
            LimiterResponse response = limiter.doFilter(new LimiterRequest(i + 2, ""));
            assertEquals(LimiterResponseStatus.ACCEPTED, response.getStatus(), "Request " + (i + 2) + " should be accepted after recovery");
        }

        assertEquals(capacity - 5, limiter.getCurrentTokens());
    }

    @Test
    void testResponseMessageAccuracy() {
        int capacity = 3;
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(capacity, 1);

        int requestId = 42;
        LimiterRequest request = new LimiterRequest(requestId, "test-content");
        LimiterResponse response = limiter.doFilter(request);

        assertEquals(LimiterResponseStatus.ACCEPTED, response.getStatus());
        assertTrue(response.getContent().contains("Request #" + requestId), "Response should contain correct request ID");
        assertTrue(response.getContent().contains(String.valueOf(capacity - 1)), "Response should contain correct token count");
        assertTrue(response.getContent().contains(String.valueOf(capacity)), "Response should contain capacity value");
    }

    @Test
    void testTokensNotLostAfterRejection() throws InterruptedException {
        int capacity = 2;
        int refillRate = 1;
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(capacity, refillRate);

        limiter.doFilter(new LimiterRequest(1, ""));
        limiter.doFilter(new LimiterRequest(2, ""));

        assertEquals(0, limiter.getCurrentTokens());

        LimiterResponse rejectedResponse = limiter.doFilter(new LimiterRequest(3, ""));
        assertEquals(LimiterResponseStatus.REJECTED, rejectedResponse.getStatus());

        assertEquals(0, limiter.getCurrentTokens(), "Rejected request should not consume tokens");

        Thread.sleep(1100);

        assertEquals(refillRate, limiter.getCurrentTokens(), "Tokens should be refilled after rejection");

        LimiterResponse acceptedResponse = limiter.doFilter(new LimiterRequest(4, ""));
        assertEquals(LimiterResponseStatus.ACCEPTED, acceptedResponse.getStatus(), "Request should be accepted after token refill");
    }
}