package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackNotificationPort {
    void sendAlert(String title, String body);
}
