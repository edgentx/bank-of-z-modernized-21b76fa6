package com.example.ports;

/**
 * Port for GitHub Issue operations.
 * Used to decouple the core logic from the actual GitHub client.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue in the repository.
     * @param title The title of the issue.
     * @param body The body of the issue.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String body);
}
