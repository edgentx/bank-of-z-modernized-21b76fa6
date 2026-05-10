package com.example.ports;

/**
 * Port interface for GitHub Integration.
 * Used by the domain to create issues without depending on concrete implementations.
 */
public interface GitHubIntegrationPort {
    String createIssue(String title, String description, String labels);
}
