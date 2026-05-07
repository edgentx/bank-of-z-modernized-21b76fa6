package com.example.application;

import com.example.domain.shared.ReportDefectCmd;
import com.example.domain.shared.SlackMessage;
import com.example.ports.SlackNotifierPort;
import org.springframework.stereotype.Service;

/**
 * Workflow service handling the defect reporting process.
 * Orchestrates the validation of inputs and notification delivery.
 * Implements the logic required to satisfy VW-454 validation.
 */
@Service
public class DefectReportingWorkflow {

    private final SlackNotifierPort slackNotifier;

    public DefectReportingWorkflow(SlackNotifierPort slackNotifier) {
        this.slackNotifier = slackNotifier;
    }

    /**
     * Executes the defect reporting workflow.
     * Validates the GitHub URL presence and formats the Slack body.
     *
     * @param cmd The command containing defect details and GitHub URL.
     * @throws IllegalArgumentException if validation fails.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        // 1. Validate inputs - Specifically ensuring URL is present as per VW-454
        if (cmd.githubIssueUrl() == null || cmd.githubIssueUrl().isBlank()) {
            throw new IllegalArgumentException("GitHub Issue URL cannot be null or empty");
        }

        // 2. Construct the Slack Body
        // Expectation: The body MUST contain the URL.
        String body = "Defect ID: " + cmd.defectId() + "\n" +
                      "GitHub Issue: " + cmd.githubIssueUrl();

        // 3. Send Notification via the port
        // In the real world, this would be injected. Here we use the mock directly to simulate.
        // We act as the orchestrator calling the port.
        slackNotifier.send(new SlackMessage("#vforce360-issues", body));
    }
}