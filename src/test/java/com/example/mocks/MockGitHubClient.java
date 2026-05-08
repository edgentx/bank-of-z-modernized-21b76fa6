package com.example.mocks;

import com.example.ports.GitHubClient;

public class MockGitHubClient implements GitHubClient {
    private String nextIssueUrl = "https://github.com/fake/issues/1";
    public boolean createIssueCalled = false;

    @Override
    public String createIssue(String repo, String title, String body) {
        createIssueCalled = true;
        return nextIssueUrl;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }
}