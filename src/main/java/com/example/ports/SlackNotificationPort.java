package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to decouple the domain logic from the specific Slack client implementation.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a specific Slack channel.
     *
     * @param channel The target channel (e.g., #vforce360-issues)
     * @param body    The message body content.
     * @return true if the message was successfully accepted by the client.
     */
    boolean postMessage(String channel, String body);
}
