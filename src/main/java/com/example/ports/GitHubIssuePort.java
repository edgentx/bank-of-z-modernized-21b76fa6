package com.example.ports;

/**
 * Port for creating GitHub issues.
 * Returns the URL of the created issue.
 */
public interface GitHubIssuePort {
    String createIssue(String title, String description, String projectId);
}
