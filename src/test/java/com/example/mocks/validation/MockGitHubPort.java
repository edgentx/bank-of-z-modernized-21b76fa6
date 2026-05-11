package com.example.mocks.validation;

import com.example.domain.validation.ports.GitHubPort;

/**
 * Mock adapter for GitHub operations.
 */
public class MockGitHubPort implements GitHubPort {
    private String mockUrl = "";

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    @Override
    public String createIssue(String defectId) {
        return mockUrl;
    }
}
