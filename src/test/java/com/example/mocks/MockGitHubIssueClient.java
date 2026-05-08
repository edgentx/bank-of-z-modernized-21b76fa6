package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import java.util.Optional;

public class MockGitHubIssueClient implements GitHubIssuePort {
    private String mockUrl;
    private boolean shouldReturnEmpty = false;

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    public void setShouldReturnEmpty(boolean shouldReturnEmpty) {
        this.shouldReturnEmpty = shouldReturnEmpty;
    }

    @Override
    public Optional<String> createIssue(String title, String description) {
        if (shouldReturnEmpty) {
            return Optional.empty();
        }
        return Optional.ofNullable(mockUrl);
    }
}
