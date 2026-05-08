package com.example.ports;

/**
 * Port interface for creating issues on GitHub.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String body, String projectLabel, String severityLabel);
}
