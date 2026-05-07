package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to verify defect VW-454.
 */
public interface SlackNotificationPort {

    /**
     * Sends a message to a Slack channel.
     *
     * @param channelId The target channel ID.
     * @param messageBody The formatted body of the message.
     * @return true if sending was successful, false otherwise.
     */
    boolean sendMessage(String channelId, String messageBody);
}