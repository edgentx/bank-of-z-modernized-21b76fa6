package com.example.ports;

/**
 * Port interface for creating GitHub issues.
 * Used by the VForce360 workflow to track reported defects.
 */
public interface GitHubIssuePort {
    
    /**
     * Creates a new issue in the GitHub repository.
     *
     * @param title The title of the issue.
     * @param description The body/description of the issue.
     * @return The HTML URL of the created issue (e.g., "https://github.com/owner/repo/issues/123").
     */
    String createIssue(String title, String description);
}
