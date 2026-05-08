package com.example.ports;

import java.util.Map;

/**
 * Port interface for sending notifications to Slack.
 * Used to mock the external Slack API in tests.
 */
public interface SlackNotificationPort {

    /**
     * Sends a notification payload to a configured Slack channel.
     *
     * @param payload A map representing the JSON body to be sent to Slack.
     *                Expected keys: "text" (String body)
     * @return true if sending was successful, false otherwise.
     */
    boolean sendNotification(Map<String, Object> payload);
}
