package com.example.ports;

/** Interface for GitHub integration */
public interface GitHubPort {
    String createIssue(String title, String description);
}
