package com.example.ports;

import java.util.Map;

/**
 * Port interface for sending Slack notifications.
 */
public interface SlackNotificationPort {
    void sendMessage(String channel, String text, Map<String, Object> attachments);
}
