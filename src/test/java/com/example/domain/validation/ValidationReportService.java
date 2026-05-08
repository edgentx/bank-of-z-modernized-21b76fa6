package com.example.domain.validation;

import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360DiagnosticPort;
import org.springframework.stereotype.Service;

/**
 * Domain Service wrapper for the validation report context.
 * This class orchestrates the fetching of diagnostic data and the notification process.
 * <p>
 * In the temporal-worker execution context, this service is responsible for:
 * 1. Gathering diagnostic context (e.g., finding the associated GitHub Issue).
 * 2. Constructing the Slack Message body including the GitHub URL.
 * 3. Sending the notification via the Slack port.
 * </p>
 * Fixes defect VW-454: GitHub URL in Slack body (end-to-end).
 */
@Service
public class ValidationReportService {
    private final SlackNotificationPort slackPort;
    private final VForce360DiagnosticPort diagnosticPort;

    public ValidationReportService(SlackNotificationPort slackPort, VForce360DiagnosticPort diagnosticPort) {
        this.slackPort = slackPort;
        this.diagnosticPort = diagnosticPort;
    }

    /**
     * Executes the defect report workflow.
     *
     * @param cmd The command containing defect details (ID, severity, description).
     */
    public void reportDefect(DefectReportCommand cmd) {
        // Phase 1: Gather diagnostic context (e.g., find associated GitHub Issue)
        String githubUrl = diagnosticPort.fetchDefectLink(cmd.id());

        // Phase 2: Construct the Slack Message
        // The defect VW-454 implies this construction logic was failing or missing the link.
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Defect Reported: ").append(cmd.id()).append("\n");
        bodyBuilder.append("Severity: ").append(cmd.severity()).append("\n");
        bodyBuilder.append("Description: ").append(cmd.description()).append("\n");

        // AC: The validation no longer exhibits the reported behavior.
        // Ensure the GitHub URL is appended if available.
        if (githubUrl != null && !githubUrl.isBlank()) {
            bodyBuilder.append("GitHub Issue: ").append(githubUrl);
        }

        // Phase 3: Send Notification
        slackPort.sendNotification(bodyBuilder.toString());
    }
}
