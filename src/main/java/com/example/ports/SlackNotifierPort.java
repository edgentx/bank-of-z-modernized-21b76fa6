package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Used by the temporal workflow to alert the engineering team.
 */
public interface SlackNotifierPort {
    /**
     * Sends a message to a configured Slack channel.
     * @param messageBody The formatted body of the message.
     */
    void send(String messageBody);
}
