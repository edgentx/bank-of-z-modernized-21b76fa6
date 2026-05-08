package com.example.application;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.shared.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Application Service / Workflow implementation for Defect reporting.
 * Orchestrates the domain logic and external notifications.
 */
@Component
public class DefectWorkflowImpl {

    private static final Logger log = LoggerFactory.getLogger(DefectWorkflowImpl.class);
    private final SlackNotificationPort slackNotificationPort;

    // Constructor injection of the port
    public DefectWorkflowImpl(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    public void reportDefect(ReportDefectCmd cmd) {
        // 1. Execute Domain Logic
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        var events = aggregate.execute(cmd);

        // 2. Process Events (Side effects)
        if (!events.isEmpty()) {
            var event = events.get(0);
            if (event instanceof com.example.domain.shared.DefectReportedEvent reportedEvent) {
                notifySlack(reportedEvent);
            }
        }
    }

    private void notifySlack(com.example.domain.shared.DefectReportedEvent event) {
        // Format payload with Slack link syntax
        // Syntax: <url|text>
        String payload = String.format(
            "Defect Reported: %s. See <%s|%s>",
            event.title(),
            event.githubUrl(),
            event.defectId()
        );

        log.info("Sending Slack notification for defect {}: {}", event.defectId(), payload);
        slackNotificationPort.send(payload);
    }
}