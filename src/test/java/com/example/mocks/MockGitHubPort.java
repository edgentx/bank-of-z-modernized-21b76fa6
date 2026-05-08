package com.example.mocks;

import com.example.ports.GitHubPort;

public class MockGitHubPort implements GitHubPort {
    private String nextIssueUrl = "https://github.com/example-bank/z-modernized/issues/454";

    @Override
    public String createIssue(String title, String description) {
        // Simulate GitHub API behavior: return a deterministic URL
        return this.nextIssueUrl;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }
}
