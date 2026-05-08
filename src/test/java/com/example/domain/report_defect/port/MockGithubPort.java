package com.example.domain.report_defect.port;

import java.time.Instant;
import java.util.UUID;

/**
 * Mock Adapter for GitHub Issue Creation.
 * Used exclusively in tests to simulate API responses without real network calls.
 */
public class MockGithubPort implements GithubIssuePort {

    private String mockUrl;
    private boolean shouldFail = false;

    public void reset() {
        this.mockUrl = null;
        this.shouldFail = false;
    }

    /**
     * Configures the URL this mock should return.
     */
    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    @Override
    public GithubIssueResponse createIssue(String title, String body, String label) {
        if (shouldFail) {
            throw new RuntimeException("Mock GitHub API Failure");
        }
        if (mockUrl == null) {
            // Default mock response if not explicitly set
            this.mockUrl = "https://github.com/mock-repo/issues/1";
        }
        // Simulate a response object
        return new GithubIssueResponse(mockUrl, UUID.randomUUID().toString(), Instant.now());
    }
}
