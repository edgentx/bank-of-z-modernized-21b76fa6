package com.example.ports;

/**
 * Port for GitHub issue operations.
 */
public interface GitHubIssuePort {

    String createIssue(String title, String description);
}