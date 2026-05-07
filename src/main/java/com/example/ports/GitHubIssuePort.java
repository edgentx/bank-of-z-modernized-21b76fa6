package com.example.ports;

import java.util.Optional;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue on GitHub.
     *
     * @param title The issue title.
     * @param description The issue description.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String description);

    /**
     * Finds the URL of an existing issue by title.
     *
     * @param title The title to search for.
     * @return Optional containing the URL if found.
     */
    Optional<String> findIssueUrlByTitle(String title);
}
