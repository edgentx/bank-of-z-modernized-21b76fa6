package com.example.ports;

/**
 * Port for creating issues in external issue trackers (e.g., GitHub).
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue.
     * @param title The issue title.
     * @param body The issue body.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String body);
}
