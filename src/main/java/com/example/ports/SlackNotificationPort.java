package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * Abstraction allows for mocking in tests and real implementation in production.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g., #vforce360-issues)
     * @param body    The formatted message body.
     * @return true if the message was successfully sent/queued, false otherwise.
     */
    boolean sendMessage(String channel, String body);
}
