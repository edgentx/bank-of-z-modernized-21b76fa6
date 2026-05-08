package com.example.ports;

/** Port for creating issues in GitHub */
public interface GitHubPort {
    String createIssue(String title, String description);
}
