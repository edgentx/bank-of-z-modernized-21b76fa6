package com.example.domain.validation.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to generate the URL for the Slack notification.
 */
public interface GitHubClient {
    String createIssue(String title, String description);
}