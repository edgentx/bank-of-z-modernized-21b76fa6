package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Used to validate defect VW-454 regarding GitHub URLs in the message body.
 */
public interface SlackPort {
    
    /**
     * Sends a notification to the #vforce360-issues channel.
     *
     * @param messageBody The formatted message payload.
     * @throws IllegalArgumentException if the messageBody is invalid or missing required components.
     */
    void sendNotification(String messageBody);
}
