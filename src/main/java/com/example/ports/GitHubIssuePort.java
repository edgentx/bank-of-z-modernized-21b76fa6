package com.example.ports;

import java.util.Optional;

/**
 * Port for creating and managing GitHub issues.
 * Used to decouple the domain logic from the GitHub API implementation.
 */
public interface GitHubIssuePort {

    /**
     * Represents a GitHub URL.
     */
    record GitHubUrl(String url) {}

    /**
     * Creates an issue in the repository based on the defect details.
     *
     * @param title The title of the issue.
     * @param description The body/description of the issue.
     * @return A GitHubUrl containing the link to the created issue, or empty if creation failed.
     */
    Optional<GitHubUrl> createIssue(String title, String description);
}