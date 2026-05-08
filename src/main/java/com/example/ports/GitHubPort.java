package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue and returns its URL.
     */
    String createIssue(String title, String body);
}