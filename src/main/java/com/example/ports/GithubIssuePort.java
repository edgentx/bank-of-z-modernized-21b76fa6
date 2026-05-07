package com.example.ports;

/**
 * Port for interacting with GitHub Issues.
 * Used by Temporal workflows/activities.
 */
public interface GithubIssuePort {

    /**
     * Creates a new issue in the configured repository.
     *
     * @param title The issue title.
     * @param body  The issue body.
     * @return The full URL of the created issue (e.g., "https://github.com/org/repo/issues/123").
     */
    String createIssue(String title, String body);
}
