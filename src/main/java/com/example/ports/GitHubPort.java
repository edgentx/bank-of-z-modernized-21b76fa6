package com.example.ports;

/**
 * Port for interacting with GitHub Issues API.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue and returns the URL to the created issue.
     *
     * @param title The issue title
     * @param description The issue body
     * @return The full URL of the created issue
     */
    String createIssue(String title, String description);
}