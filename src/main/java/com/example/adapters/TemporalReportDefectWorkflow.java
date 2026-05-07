package com.example.adapters;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import io.temporal.spring.boot.WorkflowImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the defect reporting workflow.
 * Orchestrates between GitHub and Slack.
 */
// @WorkflowImpl is a Temporal annotation for registering the workflow implementation
public class TemporalReportDefectWorkflow {

    private static final Logger log = LoggerFactory.getLogger(TemporalReportDefectWorkflow.class);

    private final GitHubPort gitHub;
    private final SlackNotificationPort slack;

    public TemporalReportDefectWorkflow(GitHubPort gitHub, SlackNotificationPort slack) {
        this.gitHub = gitHub;
        this.slack = slack;
    }

    /**
     * Executes the defect reporting flow.
     * 1. Creates an issue in GitHub.
     * 2. Notifies Slack with the issue URL appended to the body.
     *
     * @param projectId The ID of the project.
     * @param title The title of the defect.
     * @param body The body of the defect.
     */
    public void execute(String projectId, String title, String body) {
        log.info("Executing defect report for project: {}", projectId);

        // 1. Create GitHub Issue
        String issueUrl = gitHub.createIssue(title, body);
        log.info("GitHub issue created: {}", issueUrl);

        // 2. Prepare Slack body including the GitHub URL (Fix for VW-454)
        String slackBody = body + "\n" + issueUrl;

        // 3. Send Notification
        slack.sendDefectNotification(projectId, slackBody);
        log.info("Slack notification sent for project: {}", projectId);
    }
}
