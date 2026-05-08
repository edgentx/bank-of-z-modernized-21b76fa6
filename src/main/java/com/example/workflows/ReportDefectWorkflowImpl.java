package com.example.workflows;

import com.example.ports.GitHubClient;
import com.example.ports.SlackNotifier;
import io.temporal.workflow.Workflow;

import java.util.HashMap;
import java.util.Map;

/**
 * Workflow implementation for reporting a defect.
 * Orchestrates creating a GitHub issue and notifying Slack.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    // In a real Spring Boot app, these would be @Autowired.
    // Using setters for test injection (as seen in the Test setup).
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
        // 1. Create GitHub Issue
        String issueUrl = "";
        if (githubClient != null) {
            issueUrl = githubClient.createIssue("bank-of-z/legacy-issues", title, description);
        }

        // 2. Send Slack Notification
        // FIX for S-FB-1: Include the issueUrl in the Slack body text.
        if (slackNotifier != null) {
            Map<String, Object> attachment = new HashMap<>();
            // We construct the text body to explicitly include the link.
            String slackBody = "Defect Reported: " + title + "\nGitHub Issue: " + issueUrl;
            
            slackNotifier.sendNotification("https://hooks.slack.com/test", slackBody, attachment);
        }

        return "DEFECT-" + System.currentTimeMillis();
    }
}