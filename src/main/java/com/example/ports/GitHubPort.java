package com.example.ports;

/**
 * Port interface for GitHub interactions.
 * Used for creating issues or fetching metadata.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}
