package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a notification to the configured Slack channel.
     *
     * @param defectId    The ID of the defect (e.g., "VW-454")
     * @param summary     The summary of the issue
     * @param githubUrl   The URL of the created GitHub issue
     */
    void sendNotification(String defectId, String summary, String githubUrl);
}
