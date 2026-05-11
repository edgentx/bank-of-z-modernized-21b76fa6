package com.example.ports;

/**
 * Port for creating GitHub issues.
 * Used by the Temporal worker workflow during defect reporting.
 */
public interface GitHubIssueTrackerPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue
     * @param body The description of the issue
     * @return The HTML URL of the created issue
     */
    String createIssue(String title, String body);
}
