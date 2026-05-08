package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used by defect reporting workflows to create or fetch issues.
 */
public interface GitHubPort {
    
    /**
     * Creates a new issue in the configured repository.
     *
     * @param title The issue title.
     * @param body  The issue body (description).
     * @return The HTML URL of the created issue (e.g., "https://github.com/owner/repo/issues/1").
     */
    String createIssue(String title, String body);
}