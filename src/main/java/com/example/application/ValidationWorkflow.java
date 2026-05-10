package com.example.application;

import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Temporal Workflow implementation for the validation service.
 * Orchestrates the creation of a GitHub issue and subsequent Slack notification.
 */
@WorkflowImpl(taskQueue = "VALIDATION_TASK_QUEUE")
public class ValidationWorkflow {

    private static final Logger log = LoggerFactory.getLogger(ValidationWorkflow.class);

    /**
     * Reports a defect by creating a GitHub issue and sending a Slack notification.
     * 
     * @param defectId The ID of the defect (e.g., VW-454)
     * @param title The title of the defect.
     * @param description The description of the defect.
     */
    public void reportDefect(String defectId, String title, String description) {
        log.info("Starting defect reporting workflow for ID: {}", defectId);

        // 1. Create GitHub Issue
        // Activities are stubs here; the implementation is injected by Temporal Spring Starter
        ValidationActivities activities = Workflow.newActivityStub(ValidationActivities.class,
                io.temporal.activity.ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(10))
                        .build());

        String issueUrl = activities.createGitHubIssue(title, description);
        log.info("GitHub issue created: {}", issueUrl);

        // 2. Notify Slack with the URL
        // This is the core fix for VW-454: ensuring the URL is passed to the notification body
        String notificationBody = activities.formatSlackMessage(defectId, issueUrl);
        activities.sendSlackNotification(notificationBody);

        log.info("Defect reporting workflow completed for ID: {}", defectId);
    }
}
