package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Returns true if the message was successfully posted.
 */
public interface SlackNotificationPort {
    boolean postMessage(String channel, String text);
}
