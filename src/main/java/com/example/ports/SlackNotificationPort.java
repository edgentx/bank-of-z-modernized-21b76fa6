package com.example.ports;

import com.example.domain.validation.model.DefectReportedEvent;

/**
 * Port interface for Slack notifications.
 * Used to mock the external Slack API in tests.
 */
public interface SlackNotificationPort {
    void sendNotification(DefectReportedEvent event);
}
