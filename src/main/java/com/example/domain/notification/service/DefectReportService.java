package com.example.domain.notification.service;

import com.example.domain.notification.model.NotificationAggregate;
import com.example.domain.notification.model.ReportDefectCmd;
import com.example.domain.notification.model.ReportDefectEvent;
import com.example.domain.shared.DomainEvent;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application Service for handling Defect Reports.
 * Orchestrates the Aggregate logic and delegates external notifications to the Port.
 */
@Service
public class DefectReportService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportService.class);
    private final SlackNotificationPort slackNotificationPort;

    // Constructor Injection (Required by the prompt rules)
    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Entry point for Temporal or API triggers.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        // 1. Execute Aggregate Logic (Validation + Event Generation)
        // We use a generic ID for the aggregate as this is a fire-and-forget report command
        NotificationAggregate aggregate = new NotificationAggregate("cmd-" + cmd.defectId());
        List<DomainEvent> events = aggregate.execute(cmd);

        // 2. Handle Events (Side Effects)
        for (DomainEvent event : events) {
            if (event instanceof ReportDefectEvent e) {
                handleReportDefectEvent(e);
            }
        }
    }

    private void handleReportDefectEvent(ReportDefectEvent event) {
        // Format the Slack Message
        // CRITICAL for VW-454: Ensure the URL is in the body.
        String messageBody = String.format(
                "Defect Reported: %s\n" +
                "Severity: %s\n" +
                "Details: %s\n" +
                "GitHub Issue: %s", // The URL MUST be here
                event.summary(),
                event.severity(),
                event.description() != null ? event.description() : "N/A",
                event.githubIssueUrl()
        );

        String channel = "#vforce360-issues"; // Per the defect description

        boolean success = slackNotificationPort.postMessage(channel, messageBody, event.context());

        if (!success) {
            log.error("Failed to send Slack notification for defect {}", event.defectId());
            // Depending on requirements, we might throw here or retry. For now, we log.
        } else {
            log.info("Successfully reported defect {} to {}", event.defectId(), channel);
        }
    }
}