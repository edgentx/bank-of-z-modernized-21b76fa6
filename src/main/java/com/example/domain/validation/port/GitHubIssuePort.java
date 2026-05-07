package com.example.domain.validation.port;

/**
 * Port interface for creating issues in GitHub.
 * Used by the Validation Aggregate to track defects.
 */
public interface GitHubIssuePort {
    String createIssue(String title, String description);
}
