package com.example.domain.validation.ports;

import java.util.Map;

/**
 * Port for publishing messages to Slack.
 * Used to verify the message body contains the correct GitHub URL.
 */
public interface SlackPublisher {
    void publishMessage(String channel, Map<String, String> message);
}