package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.Optional;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates GitHub issue creation and URL generation.
 */
public class MockGitHubPort implements GitHubPort {

    private boolean shouldSucceed = true;
    private String mockUrlPrefix = "https://github.com/example/issues/";
    private int issueCounter = 1;

    @Override
    public Optional<String> createIssue(String title, String body) {
        if (shouldSucceed) {
            String url = mockUrlPrefix + issueCounter++;
            System.out.println("[MockGitHub] Created issue " + url + " with title: " + title);
            return Optional.of(url);
        } else {
            System.out.println("[MockGitHub] Failed to create issue.");
            return Optional.empty();
        }
    }

    public void setShouldSucceed(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
    }

    public void setMockUrlPrefix(String prefix) {
        this.mockUrlPrefix = prefix;
    }
}