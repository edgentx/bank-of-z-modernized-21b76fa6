package com.example.mocks;

import com.example.domain.validation.model.GitHubIssueUrl;
import com.example.domain.validation.port.GitHubIssuePort;
import java.util.HashSet;
import java.util.Set;

/**
 * Mock Adapter for GitHub Issue Port.
 * Simulates GitHub API behavior for testing.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private final Set<String> createdIssues = new HashSet<>();
    private GitHubIssueUrl nextUrlToReturn;

    public void setNextUrl(GitHubIssueUrl url) {
        this.nextUrlToReturn = url;
    }

    @Override
    public GitHubIssueUrl createIssue(String title, String description) {
        // Record that this was called
        String key = title + ":" + description;
        createdIssues.add(key);

        if (nextUrlToReturn == null) {
            // Default deterministic URL for testing if not set
            return new GitHubIssueUrl("https://github.com/example/bank-of-z/issues/1");
        }
        return nextUrlToReturn;
    }

    public boolean wasIssueCreated(String title, String description) {
        return createdIssues.contains(title + ":" + description);
    }

    public void reset() {
        createdIssues.clear();
        nextUrlToReturn = null;
    }
}
