package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by the validation workflow to report defects.
 */
public interface SlackNotifier {
    /**
     * Sends a message to a Slack channel.
     *
     * @param channel The target channel (e.g., "#vforce360-issues")
     * @param messageBody The content of the message (expected to contain the GitHub URL)
     */
    void send(String channel, String messageBody);
}
