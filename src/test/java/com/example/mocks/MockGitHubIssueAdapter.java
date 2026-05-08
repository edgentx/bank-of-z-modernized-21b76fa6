package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock adapter for GitHub Issues.
 * Used in testing to simulate URL generation.
 */
public class MockGitHubIssueAdapter implements GitHubIssuePort {
    private String createdTitle;
    private String createdDescription;
    private String fakeUrlPrefix = "https://github.com/example/repo/issues/";
    private int issueCount = 0;

    @Override
    public String createIssue(String title, String description) {
        this.createdTitle = title;
        this.createdDescription = description;
        this.issueCount++;
        return fakeUrlPrefix + issueCount;
    }

    public String getCreatedTitle() {
        return createdTitle;
    }

    public String getCreatedDescription() {
        return createdDescription;
    }
}