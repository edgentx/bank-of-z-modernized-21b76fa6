package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 * Used to create issues and retrieve their URLs.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue and returns the direct URL to it.
     * @param title The issue title.
     * @param body The issue body.
     * @return The HTML URL to the created issue.
     */
    String createIssue(String title, String body);
}
