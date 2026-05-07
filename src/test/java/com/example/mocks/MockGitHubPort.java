package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {

    private String nextUrl = "https://github.com/mock/issues/1";
    public String lastTitle;
    public String lastDescription;

    public void setNextUrl(String url) {
        this.nextUrl = url;
    }

    @Override
    public String createIssue(String title, String description) {
        this.lastTitle = title;
        this.lastDescription = description;
        return nextUrl;
    }
}
