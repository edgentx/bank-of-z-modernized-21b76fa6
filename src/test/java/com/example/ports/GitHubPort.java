package com.example.ports;

/**
 * Port for interacting with GitHub Issues API.
 */
public interface GitHubPort {
    /**
     * Creates a new issue in the repository.
     * @return The HTML URL of the created issue, or null if creation failed.
     */
    String createIssue(String title, String description);
}
