package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubPort {

    /**
     * Creates a GitHub issue.
     *
     * @param title The title of the issue.
     * @param body  The body description of the issue.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}
