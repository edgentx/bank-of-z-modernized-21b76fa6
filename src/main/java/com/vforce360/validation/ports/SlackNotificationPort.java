package com.vforce360.validation.ports;

/**
 * Port interface for sending Slack notifications.
 * Abstracts the specific Slack WebAPI implementation.
 */
public interface SlackNotificationPort {
    
    /**
     * Sends a message payload to a configured Slack channel.
     * @param payload The message structure including body/text.
     */
    void sendMessage(SlackMessagePayload payload);
}
