package com.example.application;

import com.example.domain.shared.DefectReportedEvent;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application Service handling the defect reporting workflow.
 * Orchestrates the reaction to domain events (sending notifications).
 */
@Service
public class DefectReportingService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingService.class);
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the DefectReportedEvent by generating the Slack message and posting it.
     * This ensures the Slack body contains the GitHub issue link as per VW-454.
     *
     * @param event The domain event containing the defect details and GitHub URL.
     */
    public void handleDefectReported(DefectReportedEvent event) {
        log.info("Processing defect reported event: {}", event.defectId());

        // Requirement: Slack body includes GitHub issue: <url>
        String messageBody = String.format(
                "Defect Reported: %s. View details: %s",
                event.defectId(),
                event.githubUrl()
        );

        slackNotificationPort.postMessage(messageBody);
    }
}
