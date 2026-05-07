package com.example.application;

import com.example.domain.shared.*;
import com.example.domain.validation.ValidationAggregate;
import com.example.ports.NotificationPort;
import org.springframework.stereotype.Service;

/**
 * Application Service handling the business logic flow for reporting defects.
 * Orchestrates the aggregate and the notification side-effects.
 */
@Service
public class DefectReportingService {

    private final NotificationPort notificationPort;

    public DefectReportingService(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    /**
     * Handles the ReportDefectCommand.
     * 1. Executes domain logic via Aggregate.
     * 2. Uses the resulting event to trigger external integrations (GitHub, then Slack).
     *
     * @param cmd The command
     */
    public void handle(ReportDefectCommand cmd) {
        ValidationAggregate aggregate = new ValidationAggregate(cmd.defectId());
        
        // Execute domain logic (Red phase: this will fail if command is invalid, but implementation is missing)
        List<DomainEvent> events = aggregate.execute(cmd);
        
        // In a real app, we would persist events here.
        
        // Process side effects
        for (DomainEvent event : events) {
            if (event instanceof DefectReportedEvent de) {
                handleDefectReported(de);
            }
        }
    }

    private void handleDefectReported(DefectReportedEvent event) {
        // Step 1: Create GitHub Issue
        String issueUrl = notificationPort.createGitHubIssue(
            "VW-454: " + event.title(), 
            event.description()
        );

        // Step 2: Notify Slack with the link (Acceptance Criteria)
        String slackBody = String.format(
            "Defect Reported: %s\nSeverity: %s\nGitHub Issue: <%s>",
            event.title(),
            event.severity(),
            issueUrl
        );

        notificationPort.postToSlack("#vforce360-issues", slackBody);
    }
}
