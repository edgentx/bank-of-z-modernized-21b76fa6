package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used by the defect reporting workflow to create tickets.
 */
public interface GitHubPort {
    
    /**
     * Creates a new issue in the configured GitHub repository.
     *
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}
