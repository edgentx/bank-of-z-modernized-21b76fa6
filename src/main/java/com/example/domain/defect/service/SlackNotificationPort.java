package com.example.domain.defect.service;

/**
 * Port for sending Slack notifications.
 * Abstracts the Slack API implementation.
 */
public interface SlackNotificationPort {
    void sendNotification(String channel, String message);
}
