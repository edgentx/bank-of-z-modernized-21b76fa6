package com.example.services;

import com.example.ports.IssueTrackingPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service implementation for DefectReportWorkflow.
 * This class acts as the System Under Test (SUT) for the E2E validation.
 * It orchestrates the flow of creating an external issue and notifying Slack.
 * 
 * <p>This follows the Adapter/Port pattern, accepting interfaces via constructor injection.</p>
 */
@Service
public class DefectReportService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportService.class);

    private final IssueTrackingPort issueTrackingPort;
    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection.
     * 
     * @param issueTrackingPort The port for interacting with GitHub/JIRA.
     * @param slackNotificationPort The port for sending Slack notifications.
     */
    public DefectReportService(IssueTrackingPort issueTrackingPort, 
                               SlackNotificationPort slackNotificationPort) {
        this.issueTrackingPort = issueTrackingPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect by creating an issue in the tracking system and notifying a Slack channel.
     * 
     * <h3>Business Logic (VW-454 Fix)</h3>
     * To resolve the defect "GitHub URL in Slack body":
     * <ol>
     *   <li>Create an issue via {@link IssueTrackingPort} and retrieve the URL.</li>
     *   <li>Construct the Slack body containing the specific URL.</li>
     *   <li>Send the notification via {@link SlackNotificationPort}.</li>
     * </ol>
     * 
     * @param targetChannel The Slack channel ID (e.g., "#vforce360-issues").
     * @param title The title of the defect.
     * @param description The description of the defect.
     */
    public void reportDefect(String targetChannel, String title, String description) {
        log.info("Reporting defect: {}", title);

        // Step 1: Create the external issue (e.g., in GitHub)
        // This returns the URL that was missing in the previous defect.
        String issueUrl = issueTrackingPort.createIssue(title, description);

        // Step 2: Compose the Slack message body ensuring the URL is present
        // We strictly format it to satisfy VW-454 validation requirements.
        String slackBody = String.format(
            "New defect reported: %s\nDescription: %s\nGitHub issue: %s",
            title, description, issueUrl
        );

        // Step 3: Send the notification
        slackNotificationPort.sendNotification(targetChannel, slackBody);

        log.info("Defect report processed. Notification sent to {} with URL: {}", targetChannel, issueUrl);
    }
}
