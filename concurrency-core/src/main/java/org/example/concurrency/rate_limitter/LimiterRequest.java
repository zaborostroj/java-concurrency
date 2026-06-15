package org.example.concurrency.rate_limitter;

import lombok.Data;

@Data
public class LimiterRequest {
    private final int id;
    private final String content;
}
