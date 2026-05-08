package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubPort {
    String createIssue(String repository, String title, String body);
    String getIssueUrl(String repository, String issueId);
}
