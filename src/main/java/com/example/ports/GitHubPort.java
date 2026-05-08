package com.example.ports;

/**
 * Port interface for GitHub issue tracking services.
 * Used by the Activity implementation to decouple from specific HTTP clients.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue.
     * @param body  The description/body of the issue.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}