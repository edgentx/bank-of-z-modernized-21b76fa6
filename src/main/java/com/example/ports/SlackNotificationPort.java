package com.example.ports;

import com.example.domain.validation.model.DefectReportedEvent;

/**
 * Port for sending Slack notifications.
 */
public interface SlackNotificationPort {
    void sendDefectNotification(DefectReportedEvent event);
}
