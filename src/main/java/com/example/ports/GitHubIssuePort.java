package com.example.ports;

import java.net.URI;

/**
 * Port interface for GitHub Issue creation.
 * Allows domain logic to create issues without depending on concrete implementations.
 */
public interface GitHubIssuePort {

    /**
     * Creates a GitHub issue for the given defect.
     *
     * @param title  The title of the issue
     * @param body   The body content of the issue
     * @return The URL of the created issue
     */
    URI createIssue(String title, String body);
}