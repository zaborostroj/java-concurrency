package org.example.concurrency.rate_limitter;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlidingWindowCounterRateLimiterTest {
    @Test
    public void testFirstRequestShouldWork() {
        SlidingWindowCounterRateLimiter rateLimiter = new SlidingWindowCounterRateLimiter(1, 1);

        var response1 = rateLimiter.doFilter(new LimiterRequest(1, "1"));
        var response2 = rateLimiter.doFilter(new LimiterRequest(2, "2"));

        assertEquals(LimiterResponseStatus.ACCEPTED, response1.getStatus(), "1 response should have ACCEPTED status, but is REJECTED");
        assertEquals(LimiterResponseStatus.REJECTED, response2.getStatus(), "1 response should have REJECTED status, but is ACCEPTED");
    }

    @Test
    public void testTokensLimit() {
        SlidingWindowCounterRateLimiter rateLimiter = new SlidingWindowCounterRateLimiter(1, 3);
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
    public void testWindowRefills() {
        SlidingWindowCounterRateLimiter rateLimiter = new SlidingWindowCounterRateLimiter(1, 3);
        LimiterRequest request = new LimiterRequest(1, "1");
        List<LimiterResponse> responses = new ArrayList<>(4);

        for (int i = 0; i < 4; i++) {
            responses.add(rateLimiter.doFilter(request));
        }

        try {
            Thread.sleep(1000);
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
}