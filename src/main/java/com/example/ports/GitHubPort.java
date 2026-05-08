package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used by the defect reporting workflow to create the remote ticket.
 */
public interface GitHubPort {
    
    /**
     * Creates a new issue in GitHub.
     * 
     * @param title The title of the issue
     * @param description The body of the issue
     * @return The HTML URL of the created issue
     */
    String createIssue(String title, String description);
}
