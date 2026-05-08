package com.example.ports;

/**
 * Port for sending notifications to Slack.
 * Used by VForce360 workflows to report defects.
 */
public interface SlackPort {
    void postMessage(String channel, String text);
}
