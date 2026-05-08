package com.example.ports;

import java.util.Map;

/**
 * Port for sending notifications to Slack.
 * Used by the domain logic to decouple from the actual Slack client implementation.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to the configured Slack channel.
     *
     * @param body The content of the message.
     * @param metadata Additional context (e.g., issue URLs, timestamps).
     * @return true if the API accepted the request, false otherwise.
     */
    boolean postMessage(String body, Map<String, String> metadata);
}
