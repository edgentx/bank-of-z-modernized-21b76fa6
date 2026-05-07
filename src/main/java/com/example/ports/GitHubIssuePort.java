package com.example.ports;

/**
 * Port for creating issues on GitHub.
 */
public interface GitHubIssuePort {
    /**
     * Creates a new issue on GitHub.
     * @param title The title of the issue
     * @param body The body of the issue
     * @return The URL of the created issue
     */
    String createIssue(String title, String body);
}