package com.example.ports;

/**
 * Port interface for interacting with GitHub Issues.
 * Used by the domain logic to decouple from the specific GitHub API implementation.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue (usually the Defect ID + Title)
     * @param description The description body
     * @return The URL of the created issue
     */
    String createIssue(String title, String description);
}
