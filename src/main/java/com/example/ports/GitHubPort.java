package com.example.ports;

/**
 * Port for GitHub Issue operations.
 */
public interface GitHubPort {
    String createIssue(String title, String description);
}
