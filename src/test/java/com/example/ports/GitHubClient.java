package com.example.ports;

/**
 * Port interface for GitHub interactions.
 * Implemented by adapters in src/main/java and mocked in tests.
 */
public interface GitHubClient {
    String createIssue(String title, String body);
}