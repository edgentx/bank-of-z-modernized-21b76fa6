package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns predictable URLs for issue creation.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String nextIssueUrl = "https://github.com/example-bank/21b76fa6-afb6-4593-9e1b-b5d7548ac4d1/issues/1";
    private String lastTitle;
    private String lastDescription;

    @Override
    public String createIssue(String title, String description) {
        this.lastTitle = title;
        this.lastDescription = description;
        return nextIssueUrl;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    public String getLastTitle() {
        return lastTitle;
    }

    public String getLastDescription() {
        return lastDescription;
    }
}
