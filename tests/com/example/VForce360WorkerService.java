package com.example;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service wrapper for the Temporal Worker logic regarding defect reporting.
 * This class acts as the System Under Test (SUT) to orchestrate the Ports.
 * In a real scenario, this would be the Workflow or Activity implementation.
 */
@Service
public class VForce360WorkerService {

    private final SlackNotificationPort slackNotificationPort;
    private final GitHubPort gitHubPort;

    public VForce360WorkerService(SlackNotificationPort slackNotificationPort, GitHubPort gitHubPort) {
        this.slackNotificationPort = slackNotificationPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Simulates the _report_defect workflow execution.
     * Expected Behavior:
     * 1. Create a GitHub issue.
     * 2. Post a notification to Slack containing the GitHub issue URL.
     */
    public void reportDefect(String title, String description) {
        // Step 1: Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(title, description);

        // Step 2: Construct Slack Body
        // The defect implies the URL was missing. We verify it is present.
        String slackBody = "Defect reported: " + title + "\n" +
                          "GitHub Issue: " + issueUrl; // FIX: Ensure URL is included

        // Step 3: Notify Slack
        slackNotificationPort.postMessage("#vforce360-issues", slackBody);
    }
}