package com.example.application;

import com.example.domain.validation.DefectAggregate;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application Service handling the defect reporting use case.
 * This acts as the glue between the Temporal trigger (or Controller) and the Domain/Ports.
 */
@Service
public class DefectReportingService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingService.class);
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the ReportDefect command.
     * 1. Executes logic via Aggregate.
     * 2. Notifies external systems (Slack) via Ports.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        
        // Execute domain logic
        var events = aggregate.execute(cmd);
        
        // Handle events (Side effects)
        for (var event : events) {
            if (event instanceof DefectReportedEvent e) {
                handleDefectReported(e);
            }
        }
    }

    private void handleDefectReported(DefectReportedEvent event) {
        // Construct the Slack Message Body
        // Requirement: "Slack body includes GitHub issue: <url>"
        String messageBody = String.format(
            "Defect Reported: %s%nGitHub issue: %s", 
            event.defectId(), 
            event.githubUrl()
        );

        // Send Notification
        boolean success = slackNotificationPort.sendMessage("#vforce360-issues", messageBody);
        
        if (!success) {
            // In a real system, we might retry or publish a Failed event.
            // For S-FB-1 fix, we ensure the URL is passed correctly.
            log.warn("Failed to send Slack notification for defect {}", event.defectId());
        }
    }
}