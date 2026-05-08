package com.example.domain.defect;

/**
 * Port interface for GitHub Issue creation.
 */
public interface GitHubPort {
    String createIssue(String title, String body);
}
