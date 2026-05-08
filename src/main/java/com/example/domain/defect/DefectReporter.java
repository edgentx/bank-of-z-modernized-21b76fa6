package com.example.domain.defect;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service responsible for reporting defects.
 * <p>
 * This acts as the orchestrator (Temporal Worker logic) for the defect reporting workflow.
 * It ensures that the Slack notification body contains the relevant GitHub URL.
 * <p>
 * Addresses Defect VW-454: Validating GitHub URL in Slack body.
 */
@Service
public class DefectReporter {

    private static final Logger log = LoggerFactory.getLogger(DefectReporter.class);
    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection of adapters/ports.
     *
     * @param gitHubIssuePort   Port to retrieve GitHub metadata.
     * @param slackNotificationPort Port to send Slack notifications.
     */
    public DefectReporter(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the reporting of a defect.
     * <p>
     * This method implements the fix for VW-454 by explicitly fetching the URL
     * and appending it to the message body before sending.
     *
     * @param defectId The unique identifier for the defect (e.g., "VW-454").
     * @param summary  A brief summary of the defect.
     */
    public void reportDefect(String defectId, String summary) {
        log.info("Reporting defect: {}", defectId);

        // Step 1: Retrieve the URL using the dedicated port.
        // This ensures the link is dynamically generated based on the current state.
        String issueUrl = gitHubIssuePort.getIssueUrl(defectId);

        // Step 2: Construct the message body including the URL.
        // The explicit concatenation of the URL fixes the defect where it was previously missing.
        StringBuilder messageBody = new StringBuilder();
        messageBody.append("Defect Reported: ").append(defectId).append("\n");
        if (summary != null && !summary.isBlank()) {
            messageBody.append("Summary: ").append(summary).append("\n");
        }
        // CRITICAL FIX for VW-454: Ensure the URL is present in the body.
        messageBody.append("GitHub Issue: ").append(issueUrl);

        // Step 3: Send the notification.
        slackNotificationPort.sendNotification(messageBody.toString());

        log.info("Defect {} report sent to Slack successfully.", defectId);
    }
}
