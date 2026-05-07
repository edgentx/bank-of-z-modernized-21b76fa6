package com.example.ports;

/**
 * Port for sending Slack notifications.
 * This abstracts the external Slack API interaction.
 */
public interface SlackNotificationPort {
    /**
     * Sends a text payload to a Slack channel.
     *
     * @param channel The target channel ID or name.
     * @param text The message body.
     */
    void sendText(String channel, String text);
}
