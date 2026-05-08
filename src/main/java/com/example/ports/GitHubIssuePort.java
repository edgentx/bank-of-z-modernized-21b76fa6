package com.example.ports;

/**
 * Port for interacting with GitHub Issues.
 * Used to create a tracking ticket for every defect reported.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new GitHub issue.
     *
     * @param title       The issue title.
     * @param description The issue body.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String description);
}