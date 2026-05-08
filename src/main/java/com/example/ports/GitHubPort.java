package com.example.ports;

/**
 * Port for creating GitHub issues.
 * Used by tests to mock GitHub interaction.
 */
public interface GitHubPort {
    String createIssue(String title, String description);
}
