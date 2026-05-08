package com.example.domain.defect.service;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.activity.Activity;
import io.temporal.activity.ActivityInterface;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import java.time.Instant;
import java.util.List;

/**
 * Temporal Workflow Implementation for reporting a defect.
 * Orchestrates GitHub issue creation and Slack notification.
 */
@WorkflowInterface
public interface DefectReportWorkflow {

    @WorkflowMethod
    String reportDefect(String title, String description);

    /**
     * Activity Interface for external interactions (GitHub, Slack).
     * Decoupled to allow mocking in tests and independent retries.
     */
    @ActivityInterface
    interface DefectActivities {
        String createGitHubIssue(String title, String description);
        boolean sendSlackNotification(String channel, String message);
    }

    /**
     * Workflow Implementation.
     * Coordinates the creation of the issue and the subsequent notification.
     */
    class DefectReportWorkflowImpl implements DefectReportWorkflow {

        private final DefectActivities activities;

        // Default constructor for Temporal
        public DefectReportWorkflowImpl() {
            this.activities = Workflow.newActivityStub(DefectActivities.class);
        }

        // Constructor for testing (dependency injection)
        public DefectReportWorkflowImpl(DefectActivities activities) {
            this.activities = activities;
        }

        @Override
        public String reportDefect(String title, String description) {
            // 1. Create GitHub Issue
            String issueUrl = activities.createGitHubIssue(title, description);

            // 2. Construct Slack Message
            // CRITICAL FIX for VW-454: Append the URL to the message body
            StringBuilder slackBody = new StringBuilder();
            slackBody.append("*New Defect Reported:");
            slackBody.append("\n*Title:* ").append(title);
            slackBody.append("\n*GitHub Issue:* ").append(issueUrl);

            // 3. Send Notification
            activities.sendSlackNotification("#vforce360-issues", slackBody.toString());

            return issueUrl;
        }
    }

    /**
     * Activity Implementation.
     * Bridges the Temporal workflow with the application's Adapters/Ports.
     */
    class DefectActivitiesImpl implements DefectActivities {

        private final GitHubPort gitHubPort;
        private final SlackPort slackPort;

        public DefectActivitiesImpl(GitHubPort gitHubPort, SlackPort slackPort) {
            this.gitHubPort = gitHubPort;
            this.slackPort = slackPort;
        }

        @Override
        public String createGitHubIssue(String title, String description) {
            try {
                return gitHubPort.createIssue(title, description);
            } catch (Exception e) {
                throw Activity.wrap(e);
            }
        }

        @Override
        public boolean sendSlackNotification(String channel, String message) {
            try {
                return slackPort.sendMessage(channel, message);
            } catch (Exception e) {
                throw Activity.wrap(e);
            }
        }
    }
}
