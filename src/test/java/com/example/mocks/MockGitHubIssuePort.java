package com.example.mocks;

import com.example.ports.GitHubIssuePort;

public class MockGitHubIssuePort implements GitHubIssuePort {
    private String baseUrl = "http://github.com/example/issues/";
    private int counter = 1;

    @Override
    public String createIssue(String title, String body) {
        // Simulate GitHub returning a URL to the new issue
        return baseUrl + counter++;
    }

    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }
}
