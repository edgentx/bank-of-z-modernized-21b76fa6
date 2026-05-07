package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.Optional;

/**
 * In-memory mock implementation of {@link GitHubPort} for testing.
 */
public class InMemoryGitHubPort implements GitHubPort {

    private String nextIssueUrl = "https://github.com/example/repo/issues/1";
    private boolean simulateFailure = false;

    @Override
    public Optional<String> createIssue(String title, String body) {
        if (simulateFailure) {
            return Optional.empty();
        }
        return Optional.of(nextIssueUrl);
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
}
