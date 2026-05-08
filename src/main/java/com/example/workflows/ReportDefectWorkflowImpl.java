package com.example.workflows;

import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Workflow implementation for reporting a defect.
 */
@WorkflowImpl(taskQueue = "DEFECT_REPORTING_TASK_QUEUE")
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final ReportDefectActivity activities;

    // Temporal requires a default constructor for instantiation, or autowiring if using the Spring Boot starter correctly.
    // We use constructor injection to ensure the activities are available.
    @Autowired
    public ReportDefectWorkflowImpl(ReportDefectActivity activities) {
        this.activities = activities;
    }

    @Override
    public String reportDefect(String description, String severity) {
        // 1. Create GitHub Issue
        String issueUrl = activities.createGitHubIssue(description, severity);

        // 2. Notify Slack with the URL
        // FIX for S-FB-1: Ensure the URL is actually passed to Slack.
        String slackMessage = "Defect reported: " + description + "\nGitHub Issue: " + issueUrl;
        activities.notifySlack(slackMessage);

        return issueUrl;
    }
}