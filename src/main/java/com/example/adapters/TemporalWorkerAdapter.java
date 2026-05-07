package com.example.adapters;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter for Temporal Worker execution logic.
 * This class orchestrates the defect reporting workflow:
 * 1. Create a GitHub Issue.
 * 2. Notify Slack with the GitHub Issue URL.
 */
public class TemporalWorkerAdapter {

    private static final Logger log = LoggerFactory.getLogger(TemporalWorkerAdapter.class);

    private final GitHubPort gitHubPort;
    private final SlackNotifierPort slackNotifierPort;

    public TemporalWorkerAdapter(GitHubPort gitHubPort, SlackNotifierPort slackNotifierPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotifierPort = slackNotifierPort;
    }

    /**
     * Simulates the workflow activity triggered by Temporal.
     * Corresponds to defect report S-FB-1: Trigger _report_defect via temporal-worker exec.
     * 
     * Expected Behavior: Slack body includes GitHub issue: <url>.
     */
    public void reportDefect(String title, String description) {
        log.info("Executing defect report for: {}", title);

        // Step 1: Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(title, description);
        log.info("GitHub issue created at: {}", issueUrl);

        // Step 2: Report to Slack with the URL
        // The defect validation checks specifically for the presence of the URL in the body.
        String messageBody = "Defect Reported. GitHub Issue: " + issueUrl;
        slackNotifierPort.notify(messageBody);

        log.info("Slack notification sent for issue: {}", issueUrl);
    }
}
