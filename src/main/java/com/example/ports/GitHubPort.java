package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GitHubPort {
    String createIssue(String title, String body);
}
