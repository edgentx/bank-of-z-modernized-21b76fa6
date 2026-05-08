package com.example.domain.defect.service;

import com.example.domain.defect.model.ReportDefectCommand;
import com.example.domain.defect.ports.SlackNotificationPort;

/**
 * Service handling the logic for reporting defects (part of the Temporal workflow).
 * In a real scenario, this would be orchestrated by Temporal activities.
 */
public class DefectReportingService {

    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports the defect to the external monitoring system (Slack).
     *
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCommand cmd) {
        // Intentionally blank implementation for Red Phase.
        // The test VW454RegressionTest should fail because this doesn't send anything yet.
    }
}
