package com.example.ports;

/**
 * Port for interacting with GitHub issue tracking.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The issue title
     * @param description The issue body/description
     * @return The HTML URL of the created issue
     */
    String createIssue(String title, String description);
}