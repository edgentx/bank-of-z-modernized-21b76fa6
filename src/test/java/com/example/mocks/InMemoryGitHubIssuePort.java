package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mock implementation of GitHubIssuePort.
 * Configurable to return success or failure states to test workflow logic.
 */
public class InMemoryGitHubIssuePort implements GitHubIssuePort {

    private String nextIssueUrl;
    private final AtomicInteger createIssueCallCount = new AtomicInteger(0);

    public InMemoryGitHubIssuePort() {
        // Default to a success state
        this.nextIssueUrl = "https://github.com/fake/repo/issues/1";
    }

    /**
     * Sets what URL should be returned by the next call.
     * Set to null to simulate a failure (Empty Optional).
     */
    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    public int getCreateIssueCallCount() {
        return createIssueCallCount.get();
    }

    @Override
    public Optional<String> createIssue(String repo, String title) {
        createIssueCallCount.incrementAndGet();
        
        if (nextIssueUrl == null) {
            return Optional.empty();
        }
        
        return Optional.of(nextIssueUrl);
    }
}
