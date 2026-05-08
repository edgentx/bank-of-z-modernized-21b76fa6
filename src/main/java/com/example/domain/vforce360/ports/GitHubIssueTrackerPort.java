package com.example.domain.vforce360.ports;

/**
 * Port for interacting with GitHub issues.
 * This abstraction allows the domain to trigger issue tracking without
 * depending directly on the GitHub API implementation.
 */
public interface GitHubIssueTrackerPort {
    /**
     * Creates a new issue in the GitHub repository.
     * @param title The title of the issue.
     * @param description The description body of the issue.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String description);
}
