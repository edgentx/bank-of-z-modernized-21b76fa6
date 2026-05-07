package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used to ensure the defect (GitHub URL in body) is verified.
 */
public interface SlackNotificationPort {

    /**
     * Posts a message to a configured Slack channel.
     *
     * @param text The body of the message.
     */
    void postMessage(String text);
}
