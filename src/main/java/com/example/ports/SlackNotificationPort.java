package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to mock the actual Slack API call during testing.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a message payload to Slack.
     * @param payload The formatted JSON string or structure representing the Slack message.
     * @return true if sending was acknowledged, false otherwise.
     */
    boolean send(String payload);
}