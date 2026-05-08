package com.example.domain.defect.service;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workflow implementation for reporting a defect.
 * Orchestrates creating a GitHub issue and notifying Slack.
 */
public class DefectReportWorkflowImpl implements DefectReportWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportWorkflowImpl.class);

    // Activities are injected by Temporal worker
    private final SlackActivities slackActivities;
    private final GitHubActivities gitHubActivities;

    public DefectReportWorkflowImpl() {
        // Default constructor required by Temporal
        this.slackActivities = Workflow.newActivityStub(SlackActivities.class);
        this.gitHubActivities = Workflow.newActivityStub(GitHubActivities.class);
    }

    // Constructor for testing/factory usage
    public DefectReportWorkflowImpl(SlackActivities slackActivities, GitHubActivities gitHubActivities) {
        this.slackActivities = slackActivities;
        this.gitHubActivities = gitHubActivities;
    }

    @Override
    public void reportDefect(String defectId, String title, String description) {
        log.info("Reporting defect {} with title '{}'", defectId, title);

        // 1. Create Issue in GitHub
        String issueUrl = gitHubActivities.createIssue(title, description);
        log.info("GitHub issue created: {}", issueUrl);

        // 2. Build Slack Message
        // Acceptance Criteria: Slack body includes GitHub issue URL
        String messageBody = "Defect Reported: " + title + "\n" + issueUrl;

        // 3. Send Notification
        slackActivities.sendNotification(messageBody);
        log.info("Slack notification sent for defect {}", defectId);
    }
}
