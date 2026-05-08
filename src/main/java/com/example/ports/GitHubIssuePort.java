package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to generate URLs and retrieve issue metadata.
 */
public interface GitHubIssuePort {
    
    /**
     * Generates the standard web URL for a GitHub issue.
     *
     * @param owner The repository owner (e.g., "example-org")
     * @param repo The repository name (e.g., "demo-repo")
     * @param issueNumber The issue number
     * @return The full HTTP URL to the issue.
     */
    String generateIssueUrl(String owner, String repo, int issueNumber);
}
