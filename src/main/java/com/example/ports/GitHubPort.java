package com.example.ports;

import java.net.URI;
import java.util.Optional;

/**
 * Port interface for creating issues in GitHub.
 * This abstraction allows us to mock the GitHub client in tests.
 */
public interface GitHubPort {
    
    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue.
     * @param body  The body content of the issue.
     * @return An Optional containing the URL of the created issue, or empty if creation failed.
     */
    Optional<URI> createIssue(String title, String body);
}
