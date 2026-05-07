package com.example.ports;

/**
 * Port for creating issues in GitHub.
 */
public interface GitHubIssuePort {
    /**
     * Creates a new issue in the repository.
     *
     * @param title The issue title.
     * @param body  The issue body.
     * @return The full URL of the created issue.
     */
    String createIssue(String title, String body);
}
