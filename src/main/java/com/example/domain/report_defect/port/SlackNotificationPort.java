package com.example.domain.report_defect.port;

/**
 * Port interface for sending Slack notifications.
 * Abstracts the Slack Web API client.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a message to a specific Slack channel.
     * @param channel The channel ID or name (e.g. #vforce360-issues)
     * @param messageBody The formatted payload (JSON or plain text depending on implementation)
     */
    void sendNotification(String channel, String messageBody);
}
