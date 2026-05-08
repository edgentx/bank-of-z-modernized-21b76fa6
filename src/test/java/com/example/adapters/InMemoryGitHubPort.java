package com.example.adapters;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Mock Adapter for GitHub.
 * Returns deterministic URLs without calling the real API.
 */
public class InMemoryGitHubPort implements GitHubPort {

    private final AtomicLong idCounter = new AtomicLong(100);
    private boolean shouldFail = false;

    @Override
    public Optional<String> createIssue(String title, String body) {
        if (shouldFail) return Optional.empty();
        long id = idCounter.getAndIncrement();
        return Optional.of("https://github.com/egdcrypto/bank-of-z/issues/" + id);
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    public String getLastGeneratedUrl() {
        return "https://github.com/egdcrypto/bank-of-z/issues/" + (idCounter.get() - 1);
    }
}