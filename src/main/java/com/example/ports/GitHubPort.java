package com.example.ports;

/**
 * Port interface for GitHub Issue management.
 * Implementations will handle REST API calls to GitHub.
 */
public interface GitHubPort {
    /**
     * Creates an issue and returns the URL.
     */
    String createIssue(String title, String description);
}
