package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.concurrent.CompletableFuture;

public class MockGitHubPort implements GitHubPort {
    private String responseUrl = "https://github.com/mock/issues/1";

    @Override
    public CompletableFuture<String> createIssue(String title, String body) {
        return CompletableFuture.completedFuture(responseUrl);
    }

    public void setResponseUrl(String url) {
        this.responseUrl = url;
    }
}
