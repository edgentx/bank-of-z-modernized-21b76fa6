package com.example.mocks;

import com.example.ports.GitHubPort;

public class MockGitHubPort implements GitHubPort {
    private String nextIssueUrl = "https://github.com/mock/issues/1";

    @Override
    public String createIssue(String title, String body) {
        return nextIssueUrl;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }
}
