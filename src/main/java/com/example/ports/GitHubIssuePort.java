package com.example.ports;

/**
 * Port interface for creating GitHub issues.
 * Returns the URL of the created issue.
 */
public interface GitHubIssuePort {
    String createIssue(String title, String description);
}
