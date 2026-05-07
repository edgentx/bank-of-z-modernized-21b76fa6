package com.example.domain.validation.port;

/**
 * Port interface for creating issues in the issue tracker (GitHub).
 */
public interface GitHubIssueTracker {
    /**
     * Creates a new issue.
     * @param summary The issue title.
     * @param description The issue body.
     * @return The URL of the created issue.
     */
    String createIssue(String summary, String description);
}
