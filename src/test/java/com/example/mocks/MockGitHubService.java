package com.example.mocks;

import com.example.application.GitHubService;

/**
 * Mock implementation of GitHubService for testing.
 * Can be configured to return specific URLs or throw exceptions.
 */
public class MockGitHubService implements GitHubService {

    private String nextUrl = "https://github.com/mock/issues/1";
    private boolean shouldFail = false;

    public void setNextUrl(String url) {
        this.nextUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    @Override
    public String createIssue(String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("Mock GitHub Service Failure");
        }
        return nextUrl;
    }
}