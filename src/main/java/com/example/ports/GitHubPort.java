package com.example.ports;

import java.net.URI;

/**
 * Port interface for creating GitHub issues.
 * This decouples the domain logic from the specific GitHub API client implementation.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title       The title of the issue.
     * @param description The body/description of the issue.
     * @return The URL of the created issue.
     */
    URI createIssue(String title, String description);
}
