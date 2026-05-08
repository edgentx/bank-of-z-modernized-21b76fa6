package com.example.ports;

/**
 * Port interface for GitHub Issue creation.
 */
public interface GitHubIssuePort {
    String createIssue(String title, String description);
}
