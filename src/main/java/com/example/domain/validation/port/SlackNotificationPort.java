package com.example.domain.validation.port;

/**
 * Port interface for sending notifications to Slack.
 * Used by the Validation Aggregate to confirm defect reporting.
 */
public interface SlackNotificationPort {
    void sendNotification(String messageBody);
}
