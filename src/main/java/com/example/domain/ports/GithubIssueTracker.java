package com.example.domain.ports;

/**
 * Port interface for interacting with GitHub Issues.
 */
public interface GithubIssueTracker {
    
    /**
     * Creates a new issue in the repository.
     * @param title The title of the issue.
     * @param description The body content of the issue.
     * @return The URL of the created issue, or null if creation failed.
     */
    String createIssue(String title, String description);
}
