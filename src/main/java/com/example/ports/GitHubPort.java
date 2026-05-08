package com.example.ports;

/**
 * Port for GitHub issue service.
 */
public interface GitHubPort {
    String createIssue(String summary, String description);
}
