package com.example.workflow;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * Service implementation for the Report Defect workflow.
 * Orchestrates the creation of a GitHub issue and subsequent notification via Slack.
 */
public class ReportDefectWorkflow {

    private static final Logger log = LoggerFactory.getLogger(ReportDefectWorkflow.class);
    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public ReportDefectWorkflow(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting process.
     * 1. Creates an issue on GitHub.
     * 2. Posts a notification to the configured Slack channel containing the GitHub URL.
     *
     * @param defectId The unique identifier of the defect (e.g., VW-454).
     * @param summary  A summary of the defect.
     * @throws Exception if underlying services fail.
     */
    public void execute(String defectId, String summary) throws Exception {
        log.info("Executing defect report for: {}", defectId);

        // 1. Create GitHub Issue
        URI issueUrl = gitHubIssuePort.createIssue(summary, "Defect reported by VForce360");

        // 2. Construct Slack Body including the URL (Fix for VW-454)
        String messageBody = String.format(
            "Defect Reported: %s\nSummary: %s\nGitHub Issue: %s",
            defectId, summary, issueUrl.toString()
        );

        // 3. Send Notification
        slackNotificationPort.send("#vforce360-issues", messageBody);
    }
}
