package com.example.ports;

/**
 * Interface for GitHub Issue tracking integration.
 */
public interface GitHubPort {
    String createIssue(String title, String description);
}
