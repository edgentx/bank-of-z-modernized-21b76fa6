package com.example.ports;

/**
 * Port interface for sending notifications to Slack.
 * This abstraction allows the core domain logic to communicate with Slack
 * without depending on specific API implementations or libraries.
 */
public interface SlackNotificationPort {
    /**
     * Sends a message body to a Slack channel.
     * @param body The formatted message content.
     */
    void send(String body);
}
