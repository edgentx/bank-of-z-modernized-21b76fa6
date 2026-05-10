package com.example.application;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Workflow implementation for reporting defects.
 * This logic is triggered by the Temporal worker to notify the team via Slack.
 */
@Service
public class DefectReportingWorkflow {

    private final SlackNotificationPort slackClient;
    private static final String SLACK_CHANNEL = "#vforce360-issues";

    /**
     * Constructor for dependency injection.
     * 
     * @param slackClient The port adapter for Slack communication.
     */
    public DefectReportingWorkflow(SlackNotificationPort slackClient) {
        this.slackClient = slackClient;
    }

    /**
     * Reports a defect to the configured Slack channel.
     * Ensures the GitHub URL is included in the body (Fix for VW-454).
     *
     * @param defectId The ID of the defect (e.g., "VW-454").
     * @param githubUrl The URL to the GitHub issue.
     * @param projectId The unique identifier of the project.
     */
    public void reportDefect(String defectId, String githubUrl, String projectId) {
        StringBuilder body = new StringBuilder();
        body.append("Defect Reported: ").append(defectId).append("\n");
        body.append("Project: ").append(projectId).append("\n");
        
        // Fix for VW-454: Append the GitHub URL to the body.
        if (githubUrl != null && !githubUrl.isBlank()) {
            body.append("GitHub issue: ").append(githubUrl).append("\n");
        }

        slackClient.sendMessage(SLACK_CHANNEL, body.toString());
    }
}
