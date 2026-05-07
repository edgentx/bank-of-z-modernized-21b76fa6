package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to verify defect VW-454.
 */
public interface GitHubPort {

    /**
     * Creates a new GitHub issue.
     *
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String body);
}