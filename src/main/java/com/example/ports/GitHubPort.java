package com.example.ports;

/**
 * Port for creating GitHub issues.
 */
public interface GitHubPort {

    /**
     * Creates a GitHub issue.
     *
     * @param title The title of the issue.
     * @param body  The body content of the issue.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String body);
}
