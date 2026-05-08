package com.example.ports;

/**
 * Port interface for Slack notifications.
 * Used by the domain to decouple from the actual Slack WebApi.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification to a channel.
     * @param channel The target channel (e.g. "#vforce360-issues")
     * @param body The message body content.
     */
    void sendMessage(String channel, String body);
}
