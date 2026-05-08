package com.example.domain.validation.port;

/**
 * Port interface for GitHub integration.
 * Used by the domain to create issues without depending on concrete implementations.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue and returns the URL of the created issue.
     *
     * @param title The title of the issue
     * @param body The body description of the issue
     * @param labels Labels to apply
     * @return The HTML URL to the created issue (e.g., https://github.com/...)
     */
    String createIssue(String title, String body, String... labels);
}
