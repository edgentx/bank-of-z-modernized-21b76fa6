package com.example.ports;

/**
 * Port for Slack notification service.
 */
public interface SlackPort {
    void sendNotification(String channel, String messageBody);
}
