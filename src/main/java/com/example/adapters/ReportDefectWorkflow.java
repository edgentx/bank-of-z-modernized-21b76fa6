package com.example.adapters;

import com.example.ports.SlackNotificationPort;

/**
 * Workflow handler for reporting defects (Temporal activity stub).
 * This represents the logic executed by the temporal-worker.
 * Implements the fix for defect S-FB-1: Ensures GitHub URL is present in Slack body.
 */
public class ReportDefectWorkflow {

    private final SlackNotificationPort slackNotificationPort;

    // Project ID is constant for this context as per the defect report
    private static final String PROJECT_ID = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";

    public ReportDefectWorkflow(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting logic.
     * 1. Constructs the message including the GitHub issue URL.
     * 2. Sends the Slack notification.
     * 
     * @param defectId The ID of the defect being reported (e.g., "VW-454").
     */
    public void execute(String defectId) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or blank");
        }

        // Construct the GitHub issue URL as per requirements
        String githubUrl = String.format("https://github.com/%s/issues/%s", PROJECT_ID, defectId);

        // Format the message body
        // Expected: "New defect reported: <URL>"
        String messageBody = "New defect reported: " + githubUrl;

        // Send the notification
        slackNotificationPort.sendNotification(messageBody);
    }
}
