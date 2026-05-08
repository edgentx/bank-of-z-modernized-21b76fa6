package com.example.ports;

/**
 * Port Interface for sending notifications to Slack.
 */
public interface SlackNotifier {
    void sendNotification(String messageBody);
}
