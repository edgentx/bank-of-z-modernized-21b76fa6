package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubPort {
    /**
     * Creates an issue in the repository.
     *
     * @param title       The issue title.
     * @param description The issue body/description.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String description);
}
