package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubRepositoryPort {
    /**
     * Creates a new issue in the repository.
     * @param title The title of the issue.
     * @param body The body of the issue.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}
