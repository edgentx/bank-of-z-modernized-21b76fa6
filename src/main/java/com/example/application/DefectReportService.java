package com.example.application;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application service handling the defect reporting process.
 * Orchestrates the aggregate logic and the subsequent notification via Slack.
 * This service acts as the 'Real Adapter' logic sitting behind the workflow/activity interface.
 */
@Service
public class DefectReportService {

    private static final Logger logger = LoggerFactory.getLogger(DefectReportService.class);
    private static final String GITHUB_BASE_URL = "https://github.com/example/bank-of-z/issues/";
    private static final String SLACK_CHANNEL = "#vforce360-issues";

    private final SlackNotificationPort slackNotificationPort;

    // Constructor injection of the port (adheres to Dependency Inversion)
    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the reporting of a defect.
     * 1. Validates logic via Aggregate.
     * 2. Constructs message containing GitHub URL.
     * 3. Sends notification via Slack Port.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        logger.info("Processing defect report: {}", cmd.defectId());

        // 1. Execute Domain Logic
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        List<DefectReportedEvent> events = aggregate.execute(cmd);

        // 2. Handle side-effects (Notification)
        for (DefectReportedEvent event : events) {
            notifySlack(event);
        }
    }

    /**
     * Constructs the Slack message body ensuring the GitHub URL is present.
     * Specifically addresses defect VW-454.
     */
    private void notifySlack(DefectReportedEvent event) {
        // Construct the GitHub URL based on the issue ID
        String githubUrl = GITHUB_BASE_URL + event.getIssueId();

        // Construct the body text
        String body = "Defect Reported: " + event.getDescription() + "\n" +
                      "Issue: " + githubUrl;

        logger.info("Sending notification to {}: {}", SLACK_CHANNEL, body);

        // Send via port
        boolean success = slackNotificationPort.sendMessage(SLACK_CHANNEL, body);

        if (!success) {
            logger.error("Failed to send Slack notification for defect {}", event.aggregateId());
            // Depending on requirements, we might throw here or retry.
            // For now, we log the failure.
        }
    }
}
