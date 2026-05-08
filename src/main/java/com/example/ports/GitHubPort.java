package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubPort {
    /**
     * Creates a new issue in the repository.
     *
     * @param title The issue title.
     * @param body  The issue body content.
     * @return The HTML URL of the created issue (e.g., "https://github.com/org/repo/issues/123").
     */
    String createIssue(String title, String body);
}
