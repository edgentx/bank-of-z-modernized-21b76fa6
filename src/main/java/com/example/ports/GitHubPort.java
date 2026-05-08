package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub Issue tracking.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue and returns its URL.
     *
     * @param title The issue title
     * @param body The issue body (severity, component, stack traces)
     * @return The full URL to the created GitHub issue, or empty if creation failed.
     */
    Optional<String> createIssue(String title, String body);
}