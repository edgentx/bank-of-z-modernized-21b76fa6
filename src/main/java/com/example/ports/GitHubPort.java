package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The issue title
     * @param body  The issue body
     * @return The HTML URL of the created issue
     */
    String createIssue(String title, String body);

    /**
     * Retrieves the base URL for the GitHub repository.
     *
     * @return The base URL (e.g. "https://github.com/org/repo")
     */
    String getRepositoryUrl();
}
