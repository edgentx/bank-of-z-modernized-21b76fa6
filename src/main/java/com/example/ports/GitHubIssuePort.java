package com.example.ports;

/**
 * Port for interacting with GitHub Issues.
 * Used to create defect reports from the VForce360 system.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue in the configured repository.
     *
     * @param title The title of the issue.
     * @param description The body description of the issue.
     * @return The HTML URL of the created issue (e.g., "https://github.com/org/repo/issues/123").
     */
    String createIssue(String title, String description);
}
