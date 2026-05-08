package com.example.steps;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Workflow/Service handling the logic for reporting defects.
 * This implementation fixes VW-454 by ensuring the GitHub URL is included in the Slack notification.
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
     * Corresponds to "Trigger _report_defect via temporal-worker exec".
     *
     * @param cmd The command containing defect details
     * @throws IllegalStateException if GitHub fails to return a URL (validation guard)
     */
    public void execute(ReportDefectCmd cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("ReportDefectCmd cannot be null");
        }

        // Step 1: Create GitHub Issue
        String issueUrl = githubIssuePort.createIssue(
            "Defect: " + cmd.defectId(),
            cmd.description()
        );

        // Validation: Ensure we have a valid URL before proceeding (Fixes null/empty scenarios)
        if (issueUrl == null || issueUrl.isBlank()) {
            throw new IllegalStateException("GitHub issue creation failed or returned an empty URL");
        }

        // Step 2: Notify Slack
        // FIX for VW-454: Explicitly append the GitHub issue URL to the body.
        String slackBody = "Defect Reported: " + cmd.defectId() + "\nGitHub issue: " + issueUrl;

        slackNotificationPort.sendNotification("#vforce360-issues", slackBody);
    }
}
