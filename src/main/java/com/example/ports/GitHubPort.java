package com.example.ports;

import java.util.concurrent.CompletableFuture;

public interface GitHubPort {
    CompletableFuture<String> createIssue(String title, String body);
}