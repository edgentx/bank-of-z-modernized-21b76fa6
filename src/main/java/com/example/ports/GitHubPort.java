package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The issue title
     * @param body The issue body
     * @return The URL of the created issue
     */
    String createIssue(String title, String body);

    /**
     * Finds the URL of an issue by title if it exists.
     *
     * @param title The title to search for
     * @return Optional containing the URL, or empty if not found
     */
    Optional<String> findIssueUrlByTitle(String title);
}