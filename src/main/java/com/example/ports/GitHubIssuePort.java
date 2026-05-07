package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue in the configured GitHub repository.
     *
     * @param title The title of the issue.
     * @param description The body content of the issue.
     * @return The full HTML URL of the created issue.
     */
    String createIssue(String title, String description);
}
