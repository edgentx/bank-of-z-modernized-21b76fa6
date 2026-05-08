package com.example.domain.vforce.port;

import com.example.domain.vforce.DefectReportingOrchestrator.SlackMessage;

/**
 * Port interface for Slack integration.
 */
public interface SlackNotificationPort {
    void notify(SlackMessage message);
}
