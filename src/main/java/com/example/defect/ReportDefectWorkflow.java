package com.example.defect;

import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@WorkflowImpl(taskQueue = "DEFECT_TASK_QUEUE")
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final SlackNotificationService slackService;

    @Autowired
    public ReportDefectWorkflowImpl(SlackNotificationService slackService) {
        this.slackService = slackService;
    }

    @Override
    public String reportDefect(String title, String severity, String githubIssueUrl) {
        // 1. Validate Input
        if (githubIssueUrl == null || !githubIssueUrl.startsWith("https://github.com/")) {
             throw new IllegalArgumentException("Invalid GitHub URL provided");
        }

        // 2. Construct Payload
        String message = String.format(
            "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
            title, severity, githubIssueUrl
        );

        // 3. Send Notification (Activity)
        slackService.sendAlert(message);

        // 4. Emit Event (Simulated for Aggregate)
        return githubIssueUrl;
    }
}