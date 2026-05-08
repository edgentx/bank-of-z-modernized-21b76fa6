package com.example.mocks;

import com.example.ports.GitHubIssuePort;

public class MockGitHubIssuePort implements GitHubIssuePort {

    private String nextIssueUrl = "https://github.com/mock/issues/1";
    public boolean createIssueCalled = false;
    public String lastTitle = "";
    public String lastDescription = "";

    @Override
    public String createIssue(String title, String description) {
        this.createIssueCalled = true;
        this.lastTitle = title;
        this.lastDescription = description;
        return nextIssueUrl;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }
}