package com.example.ports;

/**
 * Port for creating issues in GitHub.
 * Returns the HTML URL of the created issue.
 */
public interface GitHubIssuePort {
    String createIssue(String title, String description);
}
