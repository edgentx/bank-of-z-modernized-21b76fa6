package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the configured repository.
     *
     * @param title The issue title.
     * @param body  The issue body description.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}
