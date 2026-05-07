package com.example.ports;

/**
 * Port for creating GitHub issues.
 * Used by the Validation Aggregate/Service during defect reporting.
 */
public interface GitHubIssuePort {
    /**
     * Creates an issue in GitHub and returns the URL.
     * @param title The title of the issue (usually defect ID).
     * @param description The body of the issue.
     * @return The full URL to the created GitHub issue.
     */
    String createIssue(String title, String description);
}
