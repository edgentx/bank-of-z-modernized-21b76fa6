package com.example.ports;

/**
 * Port for Slack notification interactions.
 * This interface allows us to mock the external Slack API in tests.
 */
public interface SlackNotificationPort {
    void postMessage(String channel, String messageBody);
}
