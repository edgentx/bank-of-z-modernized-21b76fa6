package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application Service / Workflow Handler for Defect reporting.
 * Orchestrates the Aggregate execution and external notifications (Slack).
 * Story: S-FB-1
 */
public class DefectWorkflowService {

    private static final Logger log = LoggerFactory.getLogger(DefectWorkflowService.class);
    private final SlackNotificationPort slackNotificationPort;

    // Constructor injection for the port (Adapter Pattern requirement)
    public DefectWorkflowService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Entry point for the Temporal Worker or REST controller.
     * Handles the command, updates state, and sends notifications.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());

        // 1. Execute Domain Logic
        var events = aggregate.execute(cmd);

        // 2. Handle Side Effects (Notification)
        if (!events.isEmpty()) {
            DefectReportedEvent event = (DefectReportedEvent) events.get(0);
            notifySlack(event);
        }
    }

    private void notifySlack(DefectReportedEvent event) {
        // Formatting the Slack body to satisfy VW-454 requirements
        String messageBody = String.format(
            "Defect Reported: %s\nGitHub issue: %s",
            event.title(),
            event.githubIssueUrl()
        );

        log.info("Sending Slack notification for defect {}: {}", event.defectId(), messageBody);
        slackNotificationPort.sendNotification(messageBody);
    }
}
