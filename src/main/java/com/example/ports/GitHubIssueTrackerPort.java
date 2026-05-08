package com.example.ports;

/**
 * Port for interacting with GitHub Issue tracking.
 * Used to generate URLs for reported defects.
 */
public interface GitHubIssueTrackerPort {

    /**
     * Creates a remote issue in GitHub and returns its URL.
     *
     * @param title Title of the defect
     * @param body Description of the defect
     * @return The fully qualified URL to the GitHub issue
     */
    String createIssue(String title, String body);
}
