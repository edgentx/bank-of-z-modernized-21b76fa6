package com.example.mocks;

import com.example.ports.GitHubPort;

public class MockGitHubPort implements GitHubPort {

    private final String mockIssueUrl;

    public MockGitHubPort(String mockIssueUrl) {
        this.mockIssueUrl = mockIssueUrl;
    }

    @Override
    public String createIssue(String title, String body) {
        // Simulate GitHub issue creation by returning a pre-configured URL
        return this.mockIssueUrl;
    }
}