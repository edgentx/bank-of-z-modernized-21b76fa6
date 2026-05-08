package com.example.steps;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Workflow/Service handling the logic for reporting defects.
 * In a real scenario, this might be a Temporal Activity or Workflow implementation.
 * We are testing this class's orchestration logic.
 */
@Component
public class ReportDefectWorkflow {

    private final GitHubIssuePort githubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    // Standard Spring/Java constructor injection pattern
    public ReportDefectWorkflow(GitHubIssuePort githubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.githubIssuePort = githubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting logic.
     * This is the entry point corresponding to "Trigger _report_defect via temporal-worker exec".
     */
    public void execute(ReportDefectCmd cmd) {
        // Step 1: Create GitHub Issue
        // Note: This implementation is intentionally MISSING the logic to append the URL to Slack.
        // This ensures the test FAILS (Red Phase).
        String issueUrl = githubIssuePort.createIssue(
            "Defect: " + cmd.defectId(),
            cmd.description()
        );

        // Step 2: Notify Slack
        // DEFECT LOCATION: Currently, we might just be sending "Defect Reported" without the URL.
        // We construct the body here.
        String slackBody = "Defect Reported: " + cmd.defectId(); 
        // Missing: "GitHub issue: " + issueUrl;

        slackNotificationPort.sendNotification("#vforce360-issues", slackBody);
    }
}
