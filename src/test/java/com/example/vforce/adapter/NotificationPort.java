package com.example.vforce.adapter;

import java.util.Map;

/**
 * Port interface for sending notifications (e.g. to Slack).
 */
public interface NotificationPort {
    /**
     * Sends a message payload to the external notification system.
     * @param payload A map containing the message body and metadata.
     */
    void sendNotification(Map<String, String> payload);
}
