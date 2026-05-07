package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.UUID;

/**
 * In-memory mock implementation of GitHubPort for testing.
 * Simulates creating GitHub issues and returning predictable URLs.
 */
public class MockGitHub implements GitHubPort {

    private static final String BASE_URL = "https://github.com/example/repo/issues/";
    private String nextIssueId;

    public MockGitHub() {
        this.nextIssueId = "1";
    }

    @Override
    public String createIssue(String title, String description) {
        // Simulate successful creation and return a deterministic URL
        String url = BASE_URL + nextIssueId;
        this.nextIssueId = String.valueOf(Integer.parseInt(nextIssueId) + 1);
        return url;
    }

    /**
     * Sets the ID that will be returned for the next created issue.
     * Useful for forcing specific URLs in tests.
     */
    public void setNextIssueId(String id) {
        this.nextIssueId = id;
    }
}
