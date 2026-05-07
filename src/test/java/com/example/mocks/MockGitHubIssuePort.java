package com.example.mocks;

import com.example.domain.vforce.ports.GitHubIssuePort;

public class MockGitHubIssuePort implements GitHubIssuePort {
    private String urlToReturn = "https://github.com/test-repo/issues/1";

    @Override
    public String createIssue(String title, String body) {
        // Simulate successful creation
        return urlToReturn;
    }

    public void setUrlToReturn(String url) {
        this.urlToReturn = url;
    }
}