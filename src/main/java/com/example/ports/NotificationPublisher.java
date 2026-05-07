package com.example.ports;

/**
 * Port for publishing notifications to external systems like Slack.
 * Mocked in tests to prevent real I/O.
 */
public interface NotificationPublisher {
    void publish(String topic, String message);
}