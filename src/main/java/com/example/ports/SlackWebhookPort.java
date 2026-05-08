package com.example.ports;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackWebhookPort {
    void send(String body);
}