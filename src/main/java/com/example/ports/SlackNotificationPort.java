package com.example.ports;

/**
 * Port for sending Slack notifications.
 * Used by the Validation Aggregate/Service to broadcast defect reports.
 */
public interface SlackNotificationPort {
    /**
     * Posts a message to a specific Slack channel.
     * @param messageBody The formatted message body to send.
     */
    void postMessage(String messageBody);
}
