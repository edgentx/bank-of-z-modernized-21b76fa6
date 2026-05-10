package com.example.domain.ports;

/**
 * Port interface for interacting with GitHub.
 * Used by the domain logic to create issues without depending on concrete HTTP clients.
 */
public interface GitHubRepository {
    
    /**
     * Creates a new issue in the repository.
     * @param title The title of the issue
     * @param description The body description of the issue
     * @return The HTML URL of the created issue
     */
    String createIssue(String title, String description);
}
