package com.example.ports;

/**
 * Port interface for GitHub interactions.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue.
     * @param title The issue title.
     * @param body The issue body/description.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String body);
}