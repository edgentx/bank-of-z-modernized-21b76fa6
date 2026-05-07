package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.Optional;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {

    private String nextCreatedIssueUrl;
    private String lastCreatedTitle;
    private String lastCreatedBody;

    public void setNextCreatedIssueUrl(String url) {
        this.nextCreatedIssueUrl = url;
    }

    @Override
    public String createIssue(String title, String body) {
        this.lastCreatedTitle = title;
        this.lastCreatedBody = body;
        // Simulate returning a valid GitHub URL
        return nextCreatedIssueUrl != null ? nextCreatedIssueUrl : "https://github.com/example/repo/issues/1";
    }

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        // Not used in this specific flow, but part of the contract
        return Optional.of("https://github.com/example/repo/issues/" + issueId);
    }

    public String getLastCreatedTitle() {
        return lastCreatedTitle;
    }

    public String getLastCreatedBody() {
        return lastCreatedBody;
    }
}
