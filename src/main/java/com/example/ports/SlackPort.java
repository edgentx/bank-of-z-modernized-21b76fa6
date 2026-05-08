package com.example.ports;

import com.example.domain.validation.model.SlackNotificationMessage;

/**
 * Port for sending notifications to Slack.
 * Used by the Defect Reporting workflow to notify users.
 */
public interface SlackPort {
    void sendNotification(SlackNotificationMessage message);
}
