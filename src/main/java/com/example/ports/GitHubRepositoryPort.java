package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GitHubRepositoryPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue.
     * @param body The description/body of the issue.
     * @return The HTML URL of the created issue (e.g., https://github.com/org/repo/issues/123).
     */
    String createIssue(String title, String body);
}