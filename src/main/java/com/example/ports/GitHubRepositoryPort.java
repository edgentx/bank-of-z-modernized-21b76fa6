package com.example.ports;

import java.util.Optional;

/**
 * Port interface for interacting with GitHub issues.
 * Used by the defect reporting workflow to create and track issues.
 */
public interface GitHubRepositoryPort {

    /**
     * Records a defect in GitHub.
     *
     * @param title The title of the issue.
     * @param body The body of the issue.
     * @return The URL of the created issue.
     */
    String createIssue(String title, String body);

    /**
     * Validates if a URL is a valid GitHub issue URL format.
     * (Utility method often needed by validation logic).
     */
    boolean isValidIssueUrl(String url);
}
