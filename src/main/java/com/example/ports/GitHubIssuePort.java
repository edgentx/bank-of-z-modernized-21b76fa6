package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * This decouples the core logic from the GitHub Client implementation.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new GitHub issue.
     *
     * @param title The title of the issue
     * @param body The body content of the issue
     * @return The HTML URL of the created issue
     * @throws RuntimeException if creation fails
     */
    String createIssue(String title, String body);
}
