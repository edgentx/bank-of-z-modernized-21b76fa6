package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.Optional;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates retrieving URLs for specific issue IDs.
 */
public class MockGitHubPort implements GitHubPort {

    private String fixedUrl = "https://github.com/example/issues/1";
    private boolean shouldReturnEmpty = false;

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        if (shouldReturnEmpty) {
            return Optional.empty();
        }
        // In a real mock, we might map issueId to specific URLs.
        // For this story, we return a generic valid URL structure.
        return Optional.of("https://github.com/dummy-repo/issues/" + issueId);
    }

    @Override
    public Optional<String> createIssueAndReturnUrl(String title, String body) {
        return Optional.empty(); // Not used in this story
    }

    public void setFixedUrl(String url) {
        this.fixedUrl = url;
    }

    public void setShouldReturnEmpty(boolean isEmpty) {
        this.shouldReturnEmpty = isEmpty;
    }
}
