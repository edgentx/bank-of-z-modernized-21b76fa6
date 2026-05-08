package com.example.ports;

/**
 * Interface for GitHub issue operations.
 */
public interface GitHubPort {
    String createIssue(String title, String body, String type);
}