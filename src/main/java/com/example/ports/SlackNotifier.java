package com.example.ports;

/**
 * Port interface for sending Slack notifications.
 * Allows swapping real implementation for test mocks.
 */
public interface SlackNotifier {
    void notify(String body);
}
