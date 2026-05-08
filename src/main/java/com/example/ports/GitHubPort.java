package com.example.ports;

/**
 * Port interface for GitHub Issue creation.
 * Abstraction allows mocking in tests and swapping implementations in production.
 */
public interface GitHubPort {
    String createIssue(String title, String description);
}
