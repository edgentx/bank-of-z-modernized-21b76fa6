package com.example.ports;

/**
 * Port for creating GitHub Issues.
 */
public interface GitHubPort {
    /**
     * Creates an issue in the given repository.
     * @param repo The repository identifier (e.g., "owner/repo")
     * @param title The issue title
     * @param body The issue body
     * @return The HTML URL of the created issue
     */
    String createIssue(String repo, String title, String body);
}
