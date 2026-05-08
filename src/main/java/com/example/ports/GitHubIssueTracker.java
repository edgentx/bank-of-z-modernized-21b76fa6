package com.example.ports;

/**
 * Interface for creating GitHub issues.
 * Port definition.
 */
public interface GitHubIssueTracker {
    /**
     * Creates a GitHub issue and returns the URL of the created issue.
     */
    String createIssue(String title, String body, String label);
}
