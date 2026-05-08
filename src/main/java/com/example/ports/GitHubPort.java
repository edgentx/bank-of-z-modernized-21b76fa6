package com.example.ports;

/**
 * Port for creating issues in GitHub.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue and returns the URL.
     * @param title The issue title
     * @param body The issue body
     * @return The HTML URL of the created issue
     */
    String createIssue(String title, String body);
}
