package com.example.mocks;

import com.example.ports.GitHubIssuePort;

public class MockGitHubIssuePort implements GitHubIssuePort {

    private String nextIssueUrl = "https://github.com/example/bank-of-z/issues/1";
    public boolean createIssueCalled = false;
    public String lastTitle = "";
    public String lastBody = "";

    @Override
    public String createIssue(String title, String body) {
        this.createIssueCalled = true;
        this.lastTitle = title;
        this.lastBody = body;
        return nextIssueUrl;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    public void reset() {
        createIssueCalled = false;
        lastTitle = "";
        lastBody = "";
        nextIssueUrl = "https://github.com/example/bank-of-z/issues/1";
    }
}