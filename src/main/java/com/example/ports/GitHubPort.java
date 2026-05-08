package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 * Abstraction for the GitHub REST API client.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the configured repository.
     *
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The HTML URL of the created issue (e.g., https://github.com/owner/repo/issues/1).
     */
    String createIssue(String title, String body);
}
