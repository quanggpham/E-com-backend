package com.example.demo.service;

import com.example.demo.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReviewRateLimitService {

    private static final int MAX_REQUESTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(10);

    private final Map<Long, Deque<Instant>> requestLog = new ConcurrentHashMap<>();

    public void checkCreateReviewLimit(Long userId) {
        Instant now = Instant.now();
        Deque<Instant> timestamps = requestLog.computeIfAbsent(userId, ignored -> new ArrayDeque<>());

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(now.minus(WINDOW))) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= MAX_REQUESTS) {
                throw new BusinessException("Ban da tao review qua nhanh. Vui long thu lai sau.");
            }

            timestamps.addLast(now);
        }
    }
}
