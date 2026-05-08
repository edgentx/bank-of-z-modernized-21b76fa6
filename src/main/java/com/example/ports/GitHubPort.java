package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 * Used by the Temporal workflow to create tickets upon defect detection.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue.
     * @param title The title of the issue (e.g., Defect ID).
     * @param body The description/body of the issue.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String body);
}
