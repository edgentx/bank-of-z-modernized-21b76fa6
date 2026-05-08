package com.example.domain.validation;

import com.example.ports.SlackNotificationPort;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Temporal Workflow implementation for reporting a defect.
 * Orchestrates the notification process, ensuring the GitHub URL is included in the Slack body.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final SlackNotificationPort slackNotificationPort;

    // Constructor injection for the port (adhering to Adapter/Port pattern)
    public ReportDefectWorkflowImpl(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    @Override
    @WorkflowMethod
    public void reportDefect(String defectId, String title, String description) {
        // Logic to construct the message with the GitHub URL
        // This fixes VW-454 by ensuring the URL is formatted into the body.
        String githubUrl = "https://github.com/org/repo/issues/" + defectId.replace("-", "_").toUpperCase();
        
        // Constructing the body to strictly follow the Expected Behavior.
        // "Slack body includes GitHub issue: <url>"
        String body = String.format(
            "Defect Reported: %s\nGitHub issue: %s", 
            title, 
            githubUrl
        );

        // Send notification via the port
        slackNotificationPort.sendNotification("#vforce360-issues", body);
    }
}
