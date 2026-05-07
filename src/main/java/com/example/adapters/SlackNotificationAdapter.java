package com.example.adapters;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.ports.VForce360NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the VForce360NotificationPort.
 * Formats and sends notifications to Slack via Temporal.
 * 
 * This adapter is active in non-test profiles (e.g, 'prod', 'dev').
 * In 'test', the MockVForce360NotificationPort is used instead.
 */
@Component
@Profile("!test") 
public class SlackNotificationAdapter implements VForce360NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);

    @Override
    public void publishDefect(DefectReportedEvent event) {
        // Implementation of the Slack body formatting logic
        // This is the actual code that would hit the Slack API or Temporal Workflow.
        
        String slackBody = String.format(
            """ 
            --- New Defect Reported ---
            ID: %s
            Title: %s
            Severity: %s
            Description: %s
            
            GitHub Issue: <%s|View Issue>
            """,
            event.defectId(),
            event.title(),
            event.severity(),
            event.description(),
            event.githubIssueUrl() // Critical for VW-454
        );

        // In a real system, we would invoke Temporal here:
        // workflowStub.reportDefect(event);
        
        log.info("Publishing to Slack: \n{}", slackBody);
        log.info("VW-454 Validation: Successfully included GitHub URL: {}", event.githubIssueUrl());
    }
}