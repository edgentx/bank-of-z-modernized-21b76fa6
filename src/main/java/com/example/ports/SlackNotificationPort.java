package com.example.ports;

import com.example.domain.shared.Command;

/**
 * Port for sending notifications to Slack.
 * Used by Temporal workflows to report defects.
 */
public interface SlackNotificationPort {
    void sendDefectReport(String messageBody);
}
