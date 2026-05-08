package com.example.mocks;

import com.example.ports.GitHubClient;

import java.util.List;

/**
 * Mock implementation of GitHubClient for testing.
 * Returns predictable values to simulate successful GitHub interactions.
 */
public class MockGitHubClient implements GitHubClient {

    private String mockUrl = "https://github.com/fake-org/repo/issues/454";
    private boolean shouldFail = false;

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    @Override
    public String createIssue(String title, String body, List<String> labels) {
        if (shouldFail) {
            return null; // Simulate failure
        }
        // In a more sophisticated mock, we could verify arguments here.
        return mockUrl;
    }
}
