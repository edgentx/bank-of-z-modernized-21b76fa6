package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to generate URLs for reported defects.
 */
public interface GitHubPort {
    String createIssueUrl(String issueId);
}
