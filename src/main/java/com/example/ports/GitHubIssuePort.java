package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to create a link when reporting a defect.
 */
public interface GitHubIssuePort {

    /**
     * Creates an issue in GitHub and returns its URL.
     *
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}
