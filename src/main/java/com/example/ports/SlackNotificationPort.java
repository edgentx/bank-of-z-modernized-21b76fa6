package com.example.ports;

import com.example.domain.validation.model.DefectReportedEvent;

/**
 * Port for sending notifications to Slack.
 */
public interface SlackNotificationPort {
    void notify(DefectReportedEvent event);
}
