package com.example.ports;

/**
 * Port interface for GitHub issue operations.
 * Adapters must implement this to interact with GitHub.
 */
public interface GitHubPort {
    /**
     * Creates an issue in the repository.
     *
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @param labels Labels to apply to the issue.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body, String[] labels);
}