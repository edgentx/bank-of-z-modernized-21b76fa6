package com.example.domain.vforce.ports;

/**
 * Port for GitHub issue operations.
 * Used by the domain logic to create issues without depending on the actual GitHub client.
 */
public interface GitHubIssuePort {
    
    /**
     * Creates a new issue in the GitHub repository.
     *
     * @param title The title of the issue.
     * @param description The body content of the issue.
     * @return The HTML URL of the created issue (e.g., "https://github.com/owner/repo/issues/42").
     */
    String createIssue(String title, String description);
}
