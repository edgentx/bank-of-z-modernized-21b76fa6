package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * This abstraction allows us to mock GitHub API responses in tests.
 */
public interface GithubIssuePort {

    /**
     * Creates a new issue in the configured GitHub repository.
     *
     * @param title The title of the issue
     * @param description The body/description of the issue
     * @return The full HTML URL of the created issue
     * @throws RuntimeException if issue creation fails
     */
    String createIssue(String title, String description);
}