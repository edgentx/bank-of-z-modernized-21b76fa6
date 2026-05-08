package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by the VForce360 diagnostic workflow.
 */
public interface SlackPort {

    /**
     * Sends a message to a configured Slack channel.
     *
     * @param channel The target channel (e.g., #vforce360-issues)
     * @param body    The message body content.
     */
    void sendMessage(String channel, String body);
}
