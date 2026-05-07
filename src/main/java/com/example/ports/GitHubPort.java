package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to generate URLs for defect reporting.
 */
public interface GitHubPort {
    String createIssueUrl(String issueKey);
}
