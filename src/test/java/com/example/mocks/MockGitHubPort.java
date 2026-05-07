package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Can simulate successful issue creation or return specific URLs.
 */
public class MockGitHubPort implements GitHubPort {

    private String issueUrlToReturn = "https://github.com/example/bank-of-z/issues/1";
    private boolean createIssueCalled = false;

    @Override
    public String createIssue(String title, String body) {
        createIssueCalled = true;
        return issueUrlToReturn;
    }

    public void setIssueUrlToReturn(String url) {
        this.issueUrlToReturn = url;
    }

    public boolean isCreateIssueCalled() {
        return createIssueCalled;
    }
}
