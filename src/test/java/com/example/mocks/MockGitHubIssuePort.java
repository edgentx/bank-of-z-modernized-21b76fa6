package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Mock implementation of GitHubIssuePort for testing.
 */
@Component
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String responseUrl = "https://github.com/mock-org/mock-repo/issues/454";
    private boolean shouldFail = false;

    @Override
    public CompletableFuture<String> createIssue(String title, String body) {
        if (shouldFail) {
            return CompletableFuture.failedFuture(new RuntimeException("Simulated GitHub failure"));
        }
        return CompletableFuture.completedFuture(responseUrl);
    }

    public void setResponseUrl(String url) {
        this.responseUrl = url;
    }

    public void setSimulatedFailure(boolean fail) {
        this.shouldFail = fail;
    }
}
