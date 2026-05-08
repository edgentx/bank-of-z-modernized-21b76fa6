package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the Workflow logic to communicate with the outside world.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a Slack channel.
     *
     * @param channelId The target channel (e.g. "#vforce360-issues")
     * @param messageBody The formatted message body to send.
     * @return true if the API call was accepted, false otherwise.
     */
    boolean sendMessage(String channelId, String messageBody);
}
