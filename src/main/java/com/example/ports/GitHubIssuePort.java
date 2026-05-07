package com.example.ports;

import java.net.URI;

/**
 * Port interface for creating GitHub issues.
 */
public interface GitHubIssuePort {
    /**
     * Creates a new issue in the repository.
     * @param title The title of the issue.
     * @param description The body content of the issue.
     * @return The URL of the created issue.
     */
    URI createIssue(String title, String description);
}
