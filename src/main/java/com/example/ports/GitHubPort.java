package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The full URL of the created issue (e.g., "https://github.com/org/repo/issues/123").
     */
    String createIssue(String title, String body);

}
