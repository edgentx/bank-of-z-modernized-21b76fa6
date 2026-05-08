package com.example.ports;

/**
 * Port for interacting with GitHub (e.g., creating issues).
 */
public interface GitHubPort {
    
    /**
     * Creates a new issue in the repository.
     * @param title The issue title.
     * @param body The issue body description.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String body);
}
