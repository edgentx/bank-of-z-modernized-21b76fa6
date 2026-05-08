package com.example.ports;

import java.util.Optional;

/**
 * Port interface for interacting with GitHub issues.
 * This decouples the workflow logic from the GitHub API implementation.
 */
public interface GitHubPort {

    /**
     * Creates an issue in the repository.
     *
     * @param title       The title of the issue.
     * @param description The description body of the issue.
     * @return The URL of the created issue, or empty if creation failed.
     */
    Optional<String> createIssue(String title, String description);
}
