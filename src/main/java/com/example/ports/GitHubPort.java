package com.example.ports;

/** Port for GitHub integration */
public interface GitHubPort {
    /** Creates an issue and returns the URL */
    String createIssue(String description);
}