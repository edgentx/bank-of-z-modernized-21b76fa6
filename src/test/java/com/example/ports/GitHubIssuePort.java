package com.example.ports;

/**
 * Port for creating issues in GitHub.
 */
public interface GitHubIssuePort {
    /**
     * Creates a GitHub issue and returns the URL.
     */
    String createIssue(String title, String description);
}
