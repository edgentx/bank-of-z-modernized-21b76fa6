package com.example.ports;

/**
 * Port for creating issues in an external tracker (e.g., GitHub).
 */
public interface IssueTrackerPort {

    /**
     * Creates a new issue in the tracker.
     *
     * @param title The title of the issue
     * @param body The body content of the issue
     * @return The full URL to the created issue.
     */
    String createIssue(String title, String body);
}