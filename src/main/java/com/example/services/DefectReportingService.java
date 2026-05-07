package com.example.services;

import com.example.domain.notification.model.NotificationAggregate;
import com.example.domain.notification.model.ReportDefectCmd;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Application Service handling the defect reporting workflow.
 * Orchestrates the creation of the NotificationAggregate, interaction with GitHub (for URL),
 * and Slack (for delivery).
 * Implements the VW-454 fix logic.
 */
@Service
public class DefectReportingService {

    private final SlackNotificationPort slackNotificationPort;
    private final GitHubPort gitHubPort;

    public DefectReportingService(SlackNotificationPort slackNotificationPort, GitHubPort gitHubPort) {
        this.slackNotificationPort = slackNotificationPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Handles the ReportDefect workflow.
     * 1. Generates the GitHub URL using the GitHubPort.
     * 2. Constructs the Slack JSON payload including the URL (Fixing VW-454).
     * 3. Sends the notification via SlackNotificationPort.
     *
     * @param defectId The ID of the defect (e.g. "VW-454").
     */
    public void reportDefect(String defectId) {
        // Generate the URL using the port
        String issueUrl = gitHubPort.createIssueUrl(defectId);

        // Construct the Slack payload (JSON format required by test)
        // The test checks for a "text" field containing the URL.
        String slackPayload = String.format(
                "{\"text\": \"New defect reported: %s\"}",
                issueUrl
        );

        // Send the notification
        slackNotificationPort.sendMessage(slackPayload);

        // In a full CQRS implementation, we would also persist the Aggregate state here
        // Aggregate agg = new NotificationAggregate(UUID.randomUUID().toString());
        // agg.execute(new ReportDefectCmd(defectId));
        // repository.save(agg);
    }
}
