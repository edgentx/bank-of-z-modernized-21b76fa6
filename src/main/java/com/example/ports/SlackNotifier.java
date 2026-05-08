package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackNotifier {
    void sendNotification(String message);
}
