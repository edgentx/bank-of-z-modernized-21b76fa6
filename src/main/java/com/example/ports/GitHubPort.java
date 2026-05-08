package com.example.ports;

/**
 * Port for creating issues in GitHub.
 * Used by the Temporal workflow to report defects.
 */
public interface GitHubPort {

    /**
     * Creates a GitHub issue.
     * @param title The title of the issue
     * @param body The body of the issue
     * @return The HTML URL of the created issue
     */
    String createIssue(String title, String body);
}