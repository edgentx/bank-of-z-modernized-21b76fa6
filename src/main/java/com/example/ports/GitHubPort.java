package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in GitHub.
     *
     * @param title The title of the issue.
     * @param body  The body content of the issue.
     * @return The URL of the created issue, or empty if creation failed.
     */
    Optional<String> createIssue(String title, String body);
}