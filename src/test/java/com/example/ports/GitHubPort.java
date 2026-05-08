package com.example.ports;

/**
 * Port interface for creating GitHub issues.
 * Used by the VForce360 domain to track defects.
 */
public interface GitHubPort {
    /**
     * Creates a new issue in the GitHub repository.
     * @param title The issue title
     * @param body The issue body (description)
     * @param labels The labels to apply (e.g., "bug")
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body, String... labels);
}