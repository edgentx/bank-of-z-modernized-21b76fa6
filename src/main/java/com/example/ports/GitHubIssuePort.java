package com.example.ports;

/**
 * Port for creating GitHub issues.
 * Used to generate the tracking URL for the defect report.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue
     * @param body The description of the issue
     * @return The HTML URL of the created issue (e.g., "https://github.com/org/repo/issues/123")
     */
    String createIssue(String title, String body);
}
