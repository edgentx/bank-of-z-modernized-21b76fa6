package com.example.mocks;

import com.example.ports.GitHubClientPort;
import org.springframework.stereotype.Component;

/**
 * Mock adapter for GitHubClientPort.
 * Returns configurable data without calling the real GitHub API.
 */
@Component
public class MockGitHubClient implements GitHubClientPort {

    private String mockUrl = "https://github.com/example/bank-of-z/issues/1";

    public void setMockIssueUrl(String url) {
        this.mockUrl = url;
    }

    public String getMockIssueUrl() {
        return mockUrl;
    }

    @Override
    public String getIssueUrl(String issueId) {
        // Return the configured mock URL
        return mockUrl;
    }
}
