package com.example.domain.defect.adapter;

/**
 * Port interface for creating GitHub issues.
 */
public interface GitHubIssueTrackerAdapter {
    String createIssue(String title, String description);
}