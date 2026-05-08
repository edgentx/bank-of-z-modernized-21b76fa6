package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by Temporal workflows to report issues.
 */
public interface SlackPort {
    void postMessage(String channel, String body);
}
