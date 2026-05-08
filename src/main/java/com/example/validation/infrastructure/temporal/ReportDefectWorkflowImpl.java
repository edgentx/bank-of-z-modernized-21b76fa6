package com.example.validation.infrastructure.temporal;

import io.temporal.workflow.Workflow;

public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final SlackNotificationActivities activities = Workflow.newActivityStub(SlackNotificationActivities.class);

    @Override
    public String reportDefect(String title, String description, String severity) {
        // 1. Create GitHub Issue (Simulated)
        String issueUrl = "https://github.com/example/issues/" + Workflow.randomUUID().toString();
        
        // 2. Notify Slack
        activities.sendSlackNotification(title, issueUrl, severity);
        
        return issueUrl;
    }
}