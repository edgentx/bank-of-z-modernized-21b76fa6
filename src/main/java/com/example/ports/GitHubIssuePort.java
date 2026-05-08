package com.example.ports;

import java.util.Optional;

/**
 * Port interface for interacting with GitHub issues.
 * Used to decouple the domain logic from the GitHub API client.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue in the configured repository.
     *
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return An Optional containing the HTML URL of the created issue, or empty if creation failed.
     */
    Optional<String> createIssue(String title, String body);
}
