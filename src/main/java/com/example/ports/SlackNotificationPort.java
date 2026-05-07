package com.example.ports;

import com.example.domain.vforce.model.DefectReportedEvent;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackNotificationPort {
    void sendDefectAlert(String channel, DefectReportedEvent event);
}
