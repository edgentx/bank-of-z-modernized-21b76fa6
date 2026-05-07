package com.example.mocks;

import com.example.ports.GitHubPort;

public class MockGitHubPort implements GitHubPort {

    private String issueUrl = "https://github.com/example/repo/issues/1";
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("Failed to create issue");
        }
        return issueUrl;
    }

    public void setIssueUrl(String url) {
        this.issueUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}
