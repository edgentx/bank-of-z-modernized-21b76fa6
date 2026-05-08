package com.example.ports;

import java.util.Map;

/**
 * Port interface for sending notifications to Slack.
 */
public interface SlackPort {
    void sendMessage(Map<String, String> payload);
}
