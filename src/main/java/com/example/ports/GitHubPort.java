package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used by domain services to decouple from the actual GitHub API client.
 */
public interface GitHubPort {
    
    /**
     * Creates a new issue in the configured repository.
     *
     * @param title The title of the issue
     * @param body The body content of the issue
     * @return The HTML URL of the created issue (e.g., "https://github.com/org/repo/issues/123")
     * @throws RuntimeException if the API call fails
     */
    String createIssue(String title, String body);
}
