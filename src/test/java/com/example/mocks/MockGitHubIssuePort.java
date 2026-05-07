package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns deterministic URLs without calling the actual GitHub API.
 */
@Component
public class MockGitHubIssuePort implements GitHubIssuePort {

    private boolean isHealthy = true;
    private int issueCounter = 1;

    @Override
    public String createIssue(String title, String body) {
        // Simulate the creation of an issue and return a valid-looking URL.
        // This URL structure is what we expect to find in the Slack body.
        return "https://github.com/example/bank-of-z/issues/" + (issueCounter++);
    }

    @Override
    public public boolean isHealthy() {
        return isHealthy;
    }

    public void setHealthy(boolean healthy) {
        isHealthy = healthy;
    }
}
