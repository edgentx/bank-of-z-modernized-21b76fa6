package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub Issues.
 * Used by the VForce360 defect reporting workflow to file issues.
 */
public interface GitHubIssuePort {
    /**
     * Creates a new issue in the configured repository.
     *
     * @param title The issue title.
     * @param description The issue description/body.
     * @return A string containing the HTML URL of the created issue, or empty if failed.
     */
    Optional<String> createIssue(String title, String description);
}
