package com.example.mocks;

import com.example.ports.GitHubPort;

public class MockGitHubPort implements GitHubPort {
    private String issueUrl = "https://github.com/example/issues/1";
    private boolean shouldFail = false;

    public void setIssueUrl(String url) {
        this.issueUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    @Override
    public String createIssue(String title, String description, String component) {
        if (shouldFail) {
            throw new RuntimeException("GitHub API Error");
        }
        return issueUrl;
    }
}