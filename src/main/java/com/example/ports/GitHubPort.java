package com.example.ports;

/**
 * Port interface for GitHub Issue interactions.
 * Used for creating defect links in Slack notifications.
 */
public interface GitHubPort {

    /**
     * Creates a defect issue in GitHub and returns the URL.
     *
     * @param title The title of the defect
     * @param body The body content of the defect
     * @return The HTML URL to the created GitHub issue
     */
    String createDefectIssue(String title, String body);
}