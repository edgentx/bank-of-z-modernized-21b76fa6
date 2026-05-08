package com.example.ports;

/**
 * Port for creating GitHub issues.
 */
public interface GitHubIssuePort {
    /**
     * Creates a new issue on GitHub.
     *
     * @param title The issue title
     * @param body  The issue body
     * @return The HTML URL of the created issue (e.g., "https://github.com/org/repo/issues/123")
     */
    String createIssue(String title, String body);
}
