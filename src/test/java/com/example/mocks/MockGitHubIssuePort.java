package com.example.mocks;

import com.example.ports.GitHubIssuePort;

public class MockGitHubIssuePort implements GitHubIssuePort {
    private final String mockUrl;

    public MockGitHubIssuePort(String mockUrl) {
        this.mockUrl = mockUrl;
    }

    @Override
    public String createIssue(String title, String description) {
        return mockUrl;
    }
}
