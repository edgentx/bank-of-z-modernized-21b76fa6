package com.example.adapters;

import com.example.ports.GitHubPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

// This file will have compilation errors if dependencies are missing.
// The fix is provided in the pom.xml block.
public class OkHttpGitHubClient implements GitHubPort {
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final String repoUrl;
    private final String authToken;

    public OkHttpGitHubClient(String repoUrl, String authToken) {
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
        this.repoUrl = repoUrl;
        this.authToken = authToken;
    }

    @Override
    public CompletableFuture<String> createIssue(String title, String body) {
        // Implementation goes here - this will fail compilation without deps
        // which is expected until pom.xml is fixed.
        return CompletableFuture.completedFuture("http://fake.github.com/issue/1");
    }
}
