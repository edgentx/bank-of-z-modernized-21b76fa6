package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to generate the URL required for the defect report.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue on GitHub.
     *
     * @param title The issue title.
     * @param description The issue description/body.
     * @return The HTML URL of the created issue (e.g., "https://github.com/org/repo/issues/454").
     */
    String createIssue(String title, String description);
}
