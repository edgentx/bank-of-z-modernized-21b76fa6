package com.example.workflow;

import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360DiagnosticsPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the defect reporting workflow logic.
 * This class simulates the activity executed by the Temporal worker.
 * It acts as the bridge between the VForce360 diagnostic system and the Slack notification service.
 *
 * In a real Temporal setup, this would be an Activity implementation.
 */
public class ReportDefectWorkflowImpl {

    private static final Logger log = LoggerFactory.getLogger(ReportDefectWorkflowImpl.class);

    private final VForce360DiagnosticsPort diagnostics;
    private final SlackNotificationPort slack;

    /**
     * Constructor injection for dependencies.
     *
     * @param diagnostics Port to retrieve diagnostic data and GitHub URLs.
     * @param slack Port to send notifications.
     */
    public ReportDefectWorkflowImpl(VForce360DiagnosticsPort diagnostics, SlackNotificationPort slack) {
        this.diagnostics = diagnostics;
        this.slack = slack;
    }

    /**
     * Executes the defect reporting flow.
     * 1. Retrieves diagnostic context.
     * 2. Resolves the GitHub Issue URL.
     * 3. Formats a Slack message containing BOTH the context and the URL.
     * 4. Sends the notification.
     *
     * This fixes the defect VW-454 where the URL was missing from the Slack body.
     *
     * @param defectId The ID of the defect to report (e.g., "VW-454").
     */
    public void execute(String defectId) {
        log.info("Executing defect report workflow for ID: {}", defectId);

        // 1. Get details from VForce360
        String context = diagnostics.getDiagnosticContext(defectId);

        // 2. Resolve the GitHub URL
        String githubUrl = diagnostics.resolveGitHubUrl(defectId);

        // 3. Construct the Slack body
        // Critical fix: We explicitly append the URL to the message body.
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Defect Reported: ").append(context).append("\n");
        messageBuilder.append("GitHub Issue: <").append(githubUrl).append(">");

        String finalBody = messageBuilder.toString();

        // 4. Send to Slack
        slack.sendNotification(finalBody);

        log.info("Defect report sent to Slack successfully.");
    }
}
