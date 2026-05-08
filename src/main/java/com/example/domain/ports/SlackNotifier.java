package com.example.domain.ports;

/**
 * Port interface for Slack notifications.
 * Any implementation must be able to send a text body to a channel.
 */
public interface SlackNotifier {
    
    /**
     * Sends a notification to the configured Slack channel.
     * @param messageBody The text content of the message.
     */
    void sendNotification(String messageBody);
}
