package com.example.infrastructure.defect;

/**
 * Port interface for sending Slack notifications.
 */
public interface SlackNotifierPort {
    
    /**
     * Sends a notification to Slack with the provided message body.
     * @param messageBody The content of the Slack message
     */
    void sendNotification(String messageBody);
}
