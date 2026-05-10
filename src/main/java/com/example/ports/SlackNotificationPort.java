package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Abstracts the actual Slack API client (WebClient or OkHttp).
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to a specific Slack channel.
     *
     * @param channel The target channel ID or name (e.g., "#vforce360-issues").
     * @param messageBody The structured message body to be sent.
     * @throws IllegalArgumentException if channel or body is invalid.
     * @throws RuntimeException if the underlying API call fails.
     */
    void send(String channel, String messageBody);
}
