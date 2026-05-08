package com.example.domain.vforce360.ports;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubIssueTrackerPort {
    String createIssue(String title, String description);
}
