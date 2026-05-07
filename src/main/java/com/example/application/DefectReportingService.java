package com.example.application;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application service handling the logic for reporting defects.
 * Orchestrates the creation of a GitHub issue and the subsequent notification via Slack.
 */
public class DefectReportingService {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingService.class);

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the DefectReportedEvent by creating a GitHub issue and notifying Slack.
     *
     * @param event The domain event containing defect details.
     */
    public void handleDefectReportedEvent(DefectReportedEvent event) {
        log.info("Handling defect reported event for project: {}", event.aggregateId());

        String issueUrl;
        try {
            // 1. Create Issue in GitHub
            issueUrl = gitHubPort.createIssue(
                    event.title(),
                    formatDescription(event),
                    Map.of("severity", event.severity())
            );
            log.info("GitHub issue created: {}", issueUrl);
        } catch (Exception e) {
            log.error("Failed to create GitHub issue for defect: {}", event.defectId(), e);
            // Per requirement/verification in tests, we bubble the exception up if GitHub fails
            throw new RuntimeException("GitHub issue creation failed", e);
        }

        // 2. Notify Slack with the URL
        String slackMessage = formatSlackMessage(event, issueUrl);
        slackNotificationPort.postMessage(slackMessage);
    }

    private String formatDescription(DefectReportedEvent event) {
        return String.format(
                """ 
                **Defect ID:** %s
                **Reporter:** %s
                **Severity:** %s
                
                ---
                
                %s
                """,
                event.defectId(),
                event.reporter(),
                event.severity(),
                event.description()
        );
    }

    private String formatSlackMessage(DefectReportedEvent event, String issueUrl) {
        return String.format(
                "New defect reported for project *%s* (Severity: %s).\n" +
                "*Title:* %s\n" +
                "GitHub Issue: %s",
                event.aggregateId(),
                event.severity(),
                event.title(),
                issueUrl
        );
    }
}
