package com.example.ports;

import com.example.domain.validation.model.DefectReportedEvent;

/**
 * Port interface for sending Slack notifications.
 */
public interface SlackPort {
    void sendNotification(String channel, DefectReportedEvent event);
}
