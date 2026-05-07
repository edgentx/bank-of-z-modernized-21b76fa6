package com.example.ports;

/**
 * Port for creating GitHub issues.
 * Returns the HTML URL of the created issue to be used in notifications.
 */
public interface GitHubIssuePort {
    String createIssue(String title, String body);
}
