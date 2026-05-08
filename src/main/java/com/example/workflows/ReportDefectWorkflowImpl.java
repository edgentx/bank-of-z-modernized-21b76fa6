package com.example.workflows;

import com.example.ports.GitHubClient;
import com.example.ports.SlackNotifier;
import io.temporal.workflow.Workflow;

import java.util.HashMap;
import java.util.Map;

/**
 * Workflow implementation for reporting a defect.
 * This is a stub to satisfy compilation in the Red phase.
 * The logic to bridge GitHub and Slack is missing, causing the test to fail.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    // In a real Spring Boot app, these would be @Autowired
    private SlackNotifier slackNotifier;
    private GitHubClient githubClient;

    public void setSlackNotifier(SlackNotifier slackNotifier) {
        this.slackNotifier = slackNotifier;
    }

    public void setGithubClient(GitHubClient githubClient) {
        this.githubClient = githubClient;
    }

    @Override
    public String reportDefect(String projectId, String title, String description) {
        // Workflow Stub Implementation:
        // 1. Ideally: Create GitHub Issue
        // 2. Ideally: Send Slack Notification with the URL
        // Current State: Does nothing or partial implementation, causing test failure.

        String issueUrl = "";
        if (githubClient != null) {
             issueUrl = githubClient.createIssue("bank-of-z/legacy-issues", title, description);
        }

        // Bug location (Red Phase): We send notification, but we DON'T include the URL in the body.
        if (slackNotifier != null) {
             Map<String, Object> attachment = new HashMap<>();
             // Intentionally omitting issueUrl from the text body to reproduce the defect
             slackNotifier.sendNotification("https://hooks.slack.com/test", "Defect Reported: " + title, attachment);
        }

        return "DEFECT-" + System.currentTimeMillis();
    }
}