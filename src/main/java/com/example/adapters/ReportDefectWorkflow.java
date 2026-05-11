package com.example.adapters;

import com.example.ports.SlackNotificationPort;

/**
 * Workflow handler for reporting defects (Temporal activity stub).
 * This represents the logic executed by the temporal-worker.
 * 
 * In the Red phase, this class contains the missing implementation.
 */
public class ReportDefectWorkflow {

    private final SlackNotificationPort slackNotificationPort;

    public ReportDefectWorkflow(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting logic.
     * 1. Constructs the message.
     * 2. Sends the Slack notification.
     * 
     * @param defectId The ID of the defect being reported.
     */
    public void execute(String defectId) {
        // RED PHASE: Intentionally empty or incorrect implementation
        // to force the test to fail as per the defect description.
        // 
        // The test expects a GitHub URL in the body, so we will just
        // send a placeholder or null to ensure the test fails initially.
        
        // String message = "Defect reported: " + defectId; // Missing URL
        slackNotificationPort.sendNotification(null); 
    }
}
