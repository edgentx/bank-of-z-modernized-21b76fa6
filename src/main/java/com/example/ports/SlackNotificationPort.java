package com.example.ports;

import com.example.domain.vforce360.DefectReportedEvent;

/**
 * Port for sending Slack notifications.
 * Implementations (Real, Mock) must handle formatting and sending the message.
 */
public interface SlackNotificationPort {
    void notifyDefect(DefectReportedEvent event, String githubIssueUrl);
}