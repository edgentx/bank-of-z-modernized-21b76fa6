package com.example.workflow;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Workflow implementation for reporting defects.
 * Orchestrates the creation of a GitHub issue and the subsequent Slack notification.
 * 
 * This corresponds to the temporal-worker exec trigger mentioned in the defect.
 */
@Component
public class DefectReportingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingWorkflow.class);
    private static final String SLACK_CHANNEL = "#vforce360-issues";

    private final GitHubIssuePort githubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingWorkflow(GitHubIssuePort githubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.githubIssuePort = githubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Report a defect to GitHub and notify Slack.
     * 
     * @param title The defect title (e.g. VW-454: ...)
     * @param description The defect description.
     * @param severity The severity level (LOW, MEDIUM, HIGH).
     */
    public void reportDefect(String title, String description, String severity) {
        log.info("Executing _report_defect for: {}", title);

        // 1. Create GitHub Issue
        String issueUrl = githubIssuePort.createIssue(title, description);
        log.info("Created GitHub Issue: {}", issueUrl);

        // 2. Construct Slack Body including the GitHub URL
        // This fixes VW-454: ensuring the URL is present in the Slack body.
        String slackBody = String.format(
                "Defect Reported: %s\nSeverity: %s\nDetails: %s\nGitHub Issue: %s",
                title, severity, description, issueUrl
        );

        // 3. Send Slack Notification
        slackNotificationPort.postMessage(SLACK_CHANNEL, slackBody);
        log.info("Posted notification to {}", SLACK_CHANNEL);
    }
}
