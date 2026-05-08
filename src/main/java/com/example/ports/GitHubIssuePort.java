package com.example.ports;

/**
 * Port for interacting with GitHub Issues.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The issue title.
     * @param body  The issue body.
     * @return The HTML URL of the created issue (e.g., https://github.com/owner/repo/issues/123).
     */
    String createIssue(String title, String body);
}
