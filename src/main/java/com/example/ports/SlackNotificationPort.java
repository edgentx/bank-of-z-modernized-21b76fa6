package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Implementations must handle sending messages to a configured channel.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to Slack.
     *
     * @param payload The JSON body to send.
     * @return true if sending was attempted successfully, false otherwise.
     */
    boolean send(String payload);

    /**
     * Captures the last message body sent to Slack for verification purposes.
     * Used primarily in test mocks.
     */
    String getLastMessageBody();
}
