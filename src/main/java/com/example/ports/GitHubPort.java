package com.example.ports;

/**
 * Port for creating GitHub issues.
 */
public interface GitHubPort {
    /**
     * Creates an issue in GitHub.
     * @param title The issue title
     * @param body The issue body
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}