package com.example.ports;

/**
 * Port for interacting with GitHub Issues API.
 */
public interface GitHubPort {
    /**
     * Creates an issue in the repository and returns the URL.
     * @param title The issue title
     * @param body The issue body
     * @return The HTML URL of the created issue
     */
    String createIssue(String title, String body);
}
