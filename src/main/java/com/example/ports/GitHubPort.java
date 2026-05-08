package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * This abstraction allows us to mock GitHub API responses in tests.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the configured repository.
     *
     * @param title The issue title
     * @param body The issue body (description)
     * @return The HTML URL of the created issue
     */
    String createIssue(String title, String body);
}
