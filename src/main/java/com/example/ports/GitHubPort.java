package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue and returns the URL of the created issue.
     */
    String createIssue(String title, String description);
}