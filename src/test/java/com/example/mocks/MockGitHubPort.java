package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates GitHub issue creation without network calls.
 */
public class MockGitHubPort implements GitHubPort {

    private String fakeUrlBase = "https://github.com/example/repo/issues/";
    private int issueCounter = 1;
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("GitHub API simulated failure");
        }
        return fakeUrlBase + issueCounter++;
    }

    public void setIssueCounter(int start) {
        this.issueCounter = start;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}
