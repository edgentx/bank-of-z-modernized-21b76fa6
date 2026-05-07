package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issue tracking.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return An Optional containing the URL of the created issue, or empty if creation failed.
     */
    Optional<String> createIssue(String title, String body);
}
