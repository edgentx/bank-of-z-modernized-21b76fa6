package com.example.ports;

/**
 * Port for sending Slack notifications.
 */
public interface SlackNotifier {
    void send(String body);
}
