package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.concurrent.CompletableFuture;

public class MockGitHubPort implements GitHubPort {
    private String responseUrl = "https://github.com/test/issues/42";
    private boolean shouldFail = false;

    public void setResponseUrl(String url) {
        this.responseUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    @Override
    public CompletableFuture<String> createIssue(String title, String body) {
        if (shouldFail) {
            return CompletableFuture.failedFuture(new RuntimeException("GitHub API Error"));
        }
        return CompletableFuture.completedFuture(responseUrl);
    }
}
