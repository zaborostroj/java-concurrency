package org.example.concurrency.rate_limitter;

import lombok.Data;

@Data
public class LimiterResponse {
    private final LimiterResponseStatus status;
    private final String content;
}
