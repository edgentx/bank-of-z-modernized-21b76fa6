package com.example.domain.defect.port;

/**
 * Port for creating issues in the GitHub Issue Tracker.
 */
public interface GitHubIssueTracker {

    IssueDetails createIssue(String title, String description);

    record IssueDetails(String id, String url) {}
}
