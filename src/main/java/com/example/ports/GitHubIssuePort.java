package com.example.ports;

/**
 * Port for creating GitHub issues via VForce360 API surface.
 */
public interface GitHubIssuePort {
    /**
     * Creates a remote issue in GitHub.
     * @param title The title of the issue.
     * @param description The description/body.
     * @return The full URL to the created issue.
     */
    String createIssue(String title, String description);
}