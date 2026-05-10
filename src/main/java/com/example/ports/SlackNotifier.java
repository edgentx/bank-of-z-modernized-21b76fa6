package com.example.domain.ports;

/**
 * Port interface for sending Slack notifications.
 * Allows the domain to push messages without depending on the Slack SDK directly.
 */
public interface SlackNotifier {
    
    /**
     * Sends a notification message to a channel.
     * @param messageBody The formatted message body
     */
    void notify(String messageBody);
}
