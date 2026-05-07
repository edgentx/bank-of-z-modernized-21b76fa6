package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubIssuePort {
    
    /**
     * Creates a new issue on GitHub.
     *
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The URL of the created issue.
     * @throws IllegalArgumentException if title or body is invalid.
     */
    String createIssue(String title, String body);
}
