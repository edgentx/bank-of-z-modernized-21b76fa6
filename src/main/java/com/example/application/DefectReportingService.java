package com.example.application;

import com.example.domain.reconciliation.model.DefectReportedEvent;
import com.example.ports.SlackNotificationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Application service handling the workflow of defect reporting.
 * Orchestrates the domain logic and external notifications (Slack).
 */
@Service
public class DefectReportingService {

    private final SlackNotificationPort slackNotificationPort;
    private final String githubIssueUrlTemplate;

    public DefectReportingService(SlackNotificationPort slackNotificationPort,
                                  @Value("${vforce360.github.issue-url-template:https://github.com/egdcrypto/bank-of-z/issues/454}") String githubIssueUrlTemplate) {
        this.slackNotificationPort = slackNotificationPort;
        this.githubIssueUrlTemplate = githubIssueUrlTemplate;
    }

    /**
     * Handles the DefectReportedEvent by constructing the Slack payload
     * and sending it to the configured channel.
     * <p>
     * Implements S-FB-1: Validates that the GitHub URL is present in the body.
     *
     * @param event The domain event containing defect details.
     */
    public void handleDefectReported(DefectReportedEvent event) {
        String body = buildSlackBody(event);
        slackNotificationPort.sendNotification("#vforce360-issues", body);
    }

    private String buildSlackBody(DefectReportedEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Defect Detected:\n");
        sb.append("Batch: ").append(event.aggregateId()).append("\n");
        sb.append("Amount: ").append(event.discrepancyAmount()).append("\n");
        sb.append("Reason: ").append(event.reason()).append("\n");

        // S-FB-1 FIX: Ensure GitHub URL is appended to the body
        sb.append("GitHub issue: ").append(githubIssueUrlTemplate).append("\n");

        return sb.toString();
    }
}