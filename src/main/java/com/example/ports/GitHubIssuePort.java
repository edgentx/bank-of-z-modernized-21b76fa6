package com.example.ports;

import java.util.Optional;

/**
 * Port interface for creating GitHub issues.
 * Used to abstract the GitHub API client.
 */
public interface GitHubIssuePort {

    /**
     * Creates a GitHub issue for the given repository and title.
     *
     * @param repo The repository identifier (e.g., "owner/repo").
     * @param title The issue title.
     * @return An Optional containing the HTML URL of the created issue, or empty if creation failed.
     */
    Optional<String> createIssue(String repo, String title);
}
