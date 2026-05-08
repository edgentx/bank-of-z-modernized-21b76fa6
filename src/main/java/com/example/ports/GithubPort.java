package com.example.ports;

/**
 * Port interface for creating GitHub issues.
 * Used by the domain service to decouple from specific GitHub API implementations.
 */
public interface GithubPort {
    /**
     * Creates a GitHub issue for the given defect title and body.
     * @param title The defect title
     * @param body The defect body (including stack traces, etc)
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}
