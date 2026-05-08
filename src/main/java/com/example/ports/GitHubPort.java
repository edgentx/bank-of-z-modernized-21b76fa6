package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The issue title.
     * @param body  The issue body.
     * @return The full URL of the created issue.
     */
    String createIssue(String title, String body);
}
