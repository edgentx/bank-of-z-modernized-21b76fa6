package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by the Validation workflow to report defects.
 */
public interface SlackPort {

    /**
     * Sends a message to a Slack channel.
     *
     * @param channelId The target channel (e.g. "#vforce360-issues")
     * @param messageBody The formatted body of the message.
     */
    void sendMessage(String channelId, String messageBody);
}
