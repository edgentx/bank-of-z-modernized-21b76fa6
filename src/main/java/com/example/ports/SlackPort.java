package com.example.ports;

/**
 * Port for sending Slack notifications.
 */
public interface SlackPort {
    void sendSlackMessage(String channel, String body);
}
