package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Contract: Returns true if the message was successfully handed off to the gateway.
 */
public interface SlackNotificationPort {
    boolean postMessage(String channel, String message);
}
