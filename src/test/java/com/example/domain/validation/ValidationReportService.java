package com.example.domain.validation;

import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360DiagnosticPort;

/**
 * Domain Service wrapper for the test context.
 * This class orchestrates the fetching of diagnostic data and the notification process.
 * In the real app, this might be a Temporal Activity or a Spring Service.
 */
class ValidationReportService {
    private final SlackNotificationPort slackPort;
    private final VForce360DiagnosticPort diagnosticPort;

    public ValidationReportService(SlackNotificationPort slackPort, VForce360DiagnosticPort diagnosticPort) {
        this.slackPort = slackPort;
        this.diagnosticPort = diagnosticPort;
    }

    public void reportDefect(DefectReportCommand cmd) {
        // Phase 1: Gather diagnostic context (e.g., find associated GitHub Issue)
        String githubUrl = diagnosticPort.fetchDefectLink(cmd.id());

        // Phase 2: Construct the Slack Message
        // The defect VW-454 implies this construction logic was failing or missing.
        String body = "Defect Reported: " + cmd.id() + "\n";
        body += "Severity: " + cmd.severity() + "\n";
        body += "Description: " + cmd.description() + "\n";
        
        if (githubUrl != null) {
            body += "GitHub Issue: " + githubUrl;
        }

        // Phase 3: Send Notification
        slackPort.sendNotification(body);
    }
}