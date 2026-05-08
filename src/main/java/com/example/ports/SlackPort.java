package com.example.ports;

/**
 * Port for sending messages to Slack.
 */
public interface SlackPort {
    void sendMessage(String channel, String messageBody);
}
