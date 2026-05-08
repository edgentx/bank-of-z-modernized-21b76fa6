package com.example.application;

import com.example.domain.validation.DefectReportedEvent;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application service responsible for processing defect reports
 * and coordinating notifications (e.g., via Slack).
 */
@Service
public class DefectReportingService {

    private static final Logger logger = LoggerFactory.getLogger(DefectReportingService.class);
    private final SlackNotificationPort slackNotificationPort;

    // Constructor injection (Adapter pattern)
    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the DefectReportedEvent and formats/pushes the notification to Slack.
     * Corresponds to the temporal-worker execution context.
     */
    public void reportDefect(DefectReportedEvent event) {
        logger.info("Processing defect report: {}", event.defectId());

        String slackBody = formatSlackBody(event);
        slackNotificationPort.postMessage("#vforce360-issues", slackBody);
    }

    /**
     * Formats the Slack message body ensuring the GitHub URL is included.
     * This logic is critical for VW-454 compliance.
     */
    private String formatSlackBody(DefectReportedEvent e) {
        return String.format(
                "Defect Reported: %s\nDescription: %s\nGitHub Issue: %s",
                e.defectId(),
                e.description(),
                e.githubIssueUrl()
        );
    }
}