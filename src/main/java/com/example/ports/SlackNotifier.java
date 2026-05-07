package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by Temporal workflows and aggregates.
 */
public interface SlackNotifier {
    void send(String channel, String message);
}