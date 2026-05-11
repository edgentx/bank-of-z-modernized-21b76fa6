package com.example.ports;

/**
 * Port interface for GitHub API interaction.
 * Following the Hexagonal Architecture pattern.
 */
public interface GitHubClient {
    /**
     * Creates a GitHub issue and returns the URL of the created issue.
     */
    String createIssue(String title, String body);
}
