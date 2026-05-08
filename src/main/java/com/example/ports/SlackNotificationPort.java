package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * This is a boundary interface. The real implementation interacts with the Slack API.
 * Tests use mocks to verify interactions without real I/O.
 */
public interface SlackNotificationPort {
    
    /**
     * Posts a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g. #vforce360-issues)
     * @param body    The message body content
     */
    void postMessage(String channel, String body);
}