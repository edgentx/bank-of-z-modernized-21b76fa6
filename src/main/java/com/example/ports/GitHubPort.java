package com.example.ports;

import java.util.concurrent.CompletableFuture;

public interface GitHubPort {
    /**
     * Creates a GitHub issue asynchronously.
     * @param title The issue title
     * @param body The issue body
     * @return A CompletableFuture containing the HTML URL of the created issue
     */
    CompletableFuture<String> createIssue(String title, String body);
}
