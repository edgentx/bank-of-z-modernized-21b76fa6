package com.example.ports;

import java.net.URI;
import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue and returns the URL of the created issue.
     * Returns Optional.empty() if creation fails.
     */
    Optional<String> createIssue(String title, String body);
}
