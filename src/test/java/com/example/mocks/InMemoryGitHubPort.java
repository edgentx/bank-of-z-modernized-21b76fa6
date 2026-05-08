package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mock adapter for GitHubPort.
 * Simulates GitHub issue creation and generates valid URLs.
 */
public class InMemoryGitHubPort implements GitHubPort {

    private final AtomicInteger issueCounter = new AtomicInteger(1);

    @Override
    public String createIssue(String title, String body) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("GitHub issue title cannot be null or empty");
        }
        // Simulate returning a real URL from GitHub.com
        int id = issueCounter.getAndIncrement();
        return "https://github.com/fake-repo/project/issues/" + id;
    }
}