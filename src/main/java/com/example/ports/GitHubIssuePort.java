package com.example.ports;

/**
 * Port interface for interacting with GitHub Issues.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue in the configured repository.
     *
     * @param title The title of the issue.
     * @param body The description/body of the issue.
     * @return The HTML URL of the created issue (e.g., "https://github.com/org/repo/issues/1").
     */
    String createIssue(String title, String body);

    /**
     * Checks if the connection to GitHub is active.
     * @return true if the service is reachable.
     */
    boolean isHealthy();
}
