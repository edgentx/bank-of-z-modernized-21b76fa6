package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.Optional;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {

    private String simulatedUrl = "https://github.com/example/repo/issues/1";
    private boolean shouldFail = false;

    @Override
    public Optional<String> createIssue(String title, String body) {
        if (shouldFail) {
            return Optional.empty();
        }
        // Simulate that GitHub returns a valid URL
        return Optional.of(simulatedUrl);
    }

    public void setSimulatedUrl(String url) {
        this.simulatedUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}
