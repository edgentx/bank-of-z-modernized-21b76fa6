package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.Map;

/**
 * Mock implementation of GitHubPort for testing.
 * Allows verification of inputs and control over return values (URLs).
 */
public class MockGitHubPort implements GitHubPort {

    private String lastTitle;
    private String lastBody;
    private Map<String, String> lastLabels;
    private String mockUrl = "https://github.com/mock-repo/issues/1";
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String body, Map<String, String> labels) {
        if (shouldFail) {
            throw new RuntimeException("Mock GitHub Failure");
        }
        this.lastTitle = title;
        this.lastBody = body;
        this.lastLabels = labels;
        return mockUrl;
    }

    // Getters for Assertions
    public String getLastTitle() { return lastTitle; }
    public String getLastBody() { return lastBody; }
    public Map<String, String> getLastLabels() { return lastLabels; }

    public void setMockUrl(String url) { this.mockUrl = url; }
    public void setShouldFail(boolean fail) { this.shouldFail = fail; }
}
