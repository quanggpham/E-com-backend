package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ReviewModerationService {

    private static final Set<String> BANNED_KEYWORDS = Set.of(
            "spam",
            "lua dao",
            "scam",
            "fake",
            "doi tra ao"
    );

    public String sanitize(String content) {
        return content == null ? "" : content.trim();
    }

    public boolean containsBannedKeyword(String content) {
        String normalized = content == null ? "" : content.toLowerCase();
        return BANNED_KEYWORDS.stream().anyMatch(normalized::contains);
    }
}
