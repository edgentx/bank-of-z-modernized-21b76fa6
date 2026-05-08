package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used to ensure the Slack body contains required links (e.g., GitHub).
 */
public interface SlackPort {
    void sendNotification(String channel, String body);
}
