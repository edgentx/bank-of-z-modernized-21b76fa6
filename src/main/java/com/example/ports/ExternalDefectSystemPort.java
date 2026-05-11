package com.example.ports;

/**
 * Port interface for integrating with external defect tracking and communication systems.
 * Implementations will interact with GitHub API and Slack API.
 */
public interface ExternalDefectSystemPort {

    /**
     * Creates a new issue in the external defect tracker (GitHub) and returns the URL.
     *
     * @param title       The title of the defect.
     * @param description The detailed description of the defect.
     * @return The URL of the created GitHub issue.
     */
    String createGitHubIssue(String title, String description);

    /**
     * Sends a notification to the operations channel (Slack) confirming the defect report.
     *
     * @param message The formatted message body to send.
     */
    void sendSlackNotification(String message);
}