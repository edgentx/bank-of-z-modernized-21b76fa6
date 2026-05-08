package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GithubIssuePort {

    /**
     * Creates a new issue in the configured GitHub repository.
     *
     * @param title The title of the issue.
     * @param description The body/description of the issue.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String description);
}
