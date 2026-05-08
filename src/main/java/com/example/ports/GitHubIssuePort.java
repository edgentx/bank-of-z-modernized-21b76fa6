package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue on GitHub.
     *
     * @param title The issue title
     * @param body  The issue body/description
     * @return The URL of the created issue, or empty if creation failed
     */
    Optional<String> createIssue(String title, String body);
}