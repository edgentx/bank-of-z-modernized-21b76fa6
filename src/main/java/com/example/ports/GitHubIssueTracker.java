package com.example.ports;

/**
 * Port interface for creating GitHub issues.
 */
public interface GitHubIssueTracker {
    String createIssue(String project, String title, String description);
}
