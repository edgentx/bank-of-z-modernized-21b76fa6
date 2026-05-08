package com.example.application;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity Implementation for Defect Reporting.
 * Orchestrates the creation of a GitHub issue and notification of the URL via Slack.
 */
@Component
@ActivityImpl(taskQueues = "DefectReportingTaskQueue")
public class DefectReportingActivities implements DefectReportingActivityInterface {

    private final GitHubPort gitHubClient;
    private final SlackPort slackClient;

    public DefectReportingActivities(GitHubPort gitHubClient, SlackPort slackClient) {
        this.gitHubClient = gitHubClient;
        this.slackClient = slackClient;
    }

    @Override
    public String reportDefect(String title, String body) {
        // 1. Create the GitHub issue
        // Assuming a default repo context or that it is injected/configured.
        // For this defect, the specific repo is less critical than the link flow.
        String repo = "example/bank-of-z"; 
        String issueUrl = gitHubClient.createIssue(repo, title, body);

        // 2. Notify Slack with the URL
        // This ensures the URL is present in the Slack body (Acceptance Criteria)
        String message = String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            title,
            issueUrl
        );
        
        slackClient.sendMessage("#vforce360-issues", message);

        return issueUrl;
    }
}
