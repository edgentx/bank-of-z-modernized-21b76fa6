package com.example.ports;

/**
 * Port for GitHub interactions.
 */
public interface GitHubPort {
    String createIssue(String title, String description);
}
