package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * This abstracts the external GitHub API interaction.
 */
public interface GitHubPort {
    /**
     * Creates a new issue on GitHub.
     *
     * @param title The issue title.
     * @param body The issue body.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}
