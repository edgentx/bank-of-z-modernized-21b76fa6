package com.example.ports;

/**
 * Port for GitHub Issue operations.
 */
public interface GitHubIssuePort {
    
    /**
     * Generates the full URL for a specific issue ID.
     * @param issueId The issue ID (e.g., VW-454)
     * @return The HTTP URL string
     */
    String generateIssueUrl(String issueId);
}
