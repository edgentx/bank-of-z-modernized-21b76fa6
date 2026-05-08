package com.example.domain.defect.service;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Workflow/Service handling the defect reporting process.
 * Orchestrates the aggregate logic and subsequent notifications.
 * Implements VW-454 fix: Ensuring Slack body contains the GitHub URL.
 */
public class DefectReportWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportWorkflow.class);
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportWorkflow(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the ReportDefectCommand, executes aggregate logic,
     * and sends notifications to the configured Slack channel.
     *
     * @param cmd The command containing defect details.
     */
    public void handle(ReportDefectCommand cmd) {
        // 1. Execute Aggregate Logic
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // 2. Process Events (Side Effects)
        for (var event : events) {
            if (event instanceof DefectReportedEvent e) {
                notifySlack(e);
            }
        }
    }

    private void notifySlack(DefectReportedEvent event) {
        String channelId = "#vforce360-issues";
        
        // Construct the body. VW-454 requires the GitHub URL to be present.
        // We check if the URL exists in the event payload.
        String url = event.githubUrl();
        if (url == null || url.isBlank()) {
            // Fallback or error handling if URL is missing
            // According to VW-454, we expect a URL. If missing, we might log a warning.
            log.warn("GitHub URL missing for defect {}, sending notification without link.", event.defectId());
            slackNotificationPort.send(channelId, String.format("Defect Reported: %s (ID: %s)", event.title(), event.defectId()));
        } else {
            // Format: "GitHub issue: <url>"
            String body = String.format("GitHub issue: %s", url);
            slackNotificationPort.send(channelId, body);
        }
    }
}
