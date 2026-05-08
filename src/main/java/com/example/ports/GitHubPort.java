package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 * Used to create tickets for reported defects.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title Title of the issue.
     * @param body Body/description of the issue.
     * @param labels Labels to apply (e.g., "bug", "S-FB-1").
     * @return The full URL of the created issue.
     */
    String createIssue(String title, String body, String... labels);
}
