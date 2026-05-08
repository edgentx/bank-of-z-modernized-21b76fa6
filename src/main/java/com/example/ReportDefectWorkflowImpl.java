package com.example;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;

/**
 * Implementation of the ReportDefectWorkflow.
 * This is the "Green" phase implementation that correctly orchestrates the ports
 * to satisfy the VW-454 validation requirement: ensuring the Slack body contains
 * the actual GitHub URL, not a placeholder.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    // Constructor Injection respecting the Adapter/Port pattern
    public ReportDefectWorkflowImpl(GitHubPort gitHubPort, SlackPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    @Override
    public String execute(String title, String description) {
        // Step 1: Create the issue in GitHub
        // This call returns the actual URL of the created issue.
        String issueUrl = gitHubPort.createIssue(title, description);

        // Step 2: Notify Slack
        // CRITICAL FIX for VW-454: Pass the ACTUAL URL returned from GitHub to the Slack body,
        // rather than a static "<url>" placeholder or empty string.
        String slackBody = constructBody(issueUrl);
        slackPort.postMessage("#vforce360-issues", slackBody);

        return issueUrl;
    }

    private String constructBody(String url) {
        return "Defect Reported: " + url;
    }
}