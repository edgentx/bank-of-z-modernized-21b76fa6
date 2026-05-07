package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by VForce360 diagnostic workflow handlers.
 */
public interface SlackPort {
    void postMessage(String channel, String body);
}
