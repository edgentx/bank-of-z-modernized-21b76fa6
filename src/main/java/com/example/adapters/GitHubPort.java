package com.example.adapters;

/**
 * Port interface for GitHub Issue creation.
 */
public interface GitHubPort {
    String createIssue(String title, String body);
}
