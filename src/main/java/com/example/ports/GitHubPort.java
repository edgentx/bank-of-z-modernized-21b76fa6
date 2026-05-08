package com.example.ports;

/**
 * Port interface for GitHub interactions.
 */
public interface GitHubPort {
    String createIssue(String title, String description);
}
