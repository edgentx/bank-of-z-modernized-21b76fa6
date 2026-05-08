package com.example.mocks;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Mock GitHub Adapter for testing.
 * Returns predictable URLs without making network calls.
 */
@Component
public class MockGitHubClient implements GitHubPort {

    private boolean createIssueCalled = false;
    private String lastUrl;

    @Override
    public String createIssue(String repo, String title, String body) {
        this.createIssueCalled = true;
        // Simulate GitHub returning a real URL
        this.lastUrl = "https://github.com/" + repo + "/issues/" + UUID.randomUUID().toString();
        return this.lastUrl;
    }

    public boolean wasCreateIssueCalled() {
        return createIssueCalled;
    }

    public String getLastGeneratedIssueUrl() {
        return lastUrl;
    }

    public void reset() {
        this.createIssueCalled = false;
        this.lastUrl = null;
    }
}
