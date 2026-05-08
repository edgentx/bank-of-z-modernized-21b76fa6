package com.example.ports;

/**
 * Port interface for GitHub Issue tracking integration.
 * Decouples the domain from the GitHub API client implementation.
 */
public interface GitHubPort {
    String createIssue(String title, String description, String severity);
}
