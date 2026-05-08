package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Used by the Validation Aggregate to externalize side effects.
 */
public interface SlackNotifier {
    
    /**
     * Sends a notification message to a configured Slack channel.
     * @param messageBody The formatted message to send.
     */
    void sendNotification(String messageBody);
}
