package com.example.domain.validation;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Workflow service responsible for reporting defects.
 * This class orchestrates the logic of notifying external systems (like Slack)
 * when a defect is identified or validated.
 */
@Service
public class DefectReportingWorkflow {

    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor for dependency injection.
     * Uses the Port interface to allow for mock implementations during testing
     * and real adapters (e.g., SlackApiAdapter) in production.
     *
     * @param slackNotificationPort The port handling Slack notifications.
     */
    public DefectReportingWorkflow(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect to the VForce360 issue channel.
     * <p>
     * Corresponds to the "_report_defect via temporal-worker exec" trigger.
     * </p>
     *
     * @param defectId The unique identifier of the defect (e.g., "VW-454").
     * @param githubUrl The direct URL to the GitHub issue.
     */
    public void reportDefect(String defectId, String githubUrl) {
        String channel = "#vforce360-issues";
        
        // Construct the message body ensuring the GitHub URL is present.
        // Requirement: "Slack body includes GitHub issue: <url>"
        StringBuilder messageBody = new StringBuilder();
        messageBody.append("Defect Reported: ").append(defectId).append("\n");
        messageBody.append("GitHub issue: ").append(githubUrl);

        slackNotificationPort.sendMessage(channel, messageBody.toString());
    }
}
