package com.example.ports;

/**
 * Port interface for interacting with Issue Tracking systems (e.g., GitHub).
 */
public interface IssueTrackerPort {

    /**
     * Creates a new issue in the tracker.
     *
     * @param title The title of the issue.
     * @param description The body content of the issue.
     * @return The unique URL of the created issue.
     */
    String createIssue(String title, String description);
}
