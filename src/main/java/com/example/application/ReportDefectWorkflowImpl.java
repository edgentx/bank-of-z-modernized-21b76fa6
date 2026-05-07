package com.example.application;

import com.example.domain.shared.Command;
import com.example.ports.GithubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Report Defect workflow logic.
 * This class orchestrates the creation of a GitHub issue and the subsequent
 * Slack notification, ensuring the Slack body contains the GitHub URL.
 * 
 * Corresponds to Story S-FB-1 / Defect VW-454.
 */
@Component
public class ReportDefectWorkflowImpl {

    private final GithubIssuePort githubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructor injection of ports (dependencies).
     * 
     * @param githubIssuePort The port to interact with GitHub.
     * @param slackNotificationPort The port to send Slack messages.
     */
    public ReportDefectWorkflowImpl(GithubIssuePort githubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.githubIssuePort = githubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting process.
     * 1. Creates an issue on GitHub.
     * 2. Posts a notification to Slack with the GitHub URL included in the body.
     * 
     * @param cmd The command triggering the defect report. (Currently unused in logic but part of domain signature, keeping for consistency if domain objects are passed).
     * @param title The title of the defect.
     * @param body The description/body of the defect.
     * @param channel The target Slack channel.
     */
    public void reportDefect(Command cmd, String title, String body, String channel) {
        // 1. Create GitHub Issue
        // We expect the GithubIssuePort to return the full URL of the created issue.
        String githubUrl = githubIssuePort.createIssue(title, body);

        // 2. Construct Slack Body
        // Requirement VW-454: The body MUST contain the URL.
        String slackBody = "New Defect Reported: " + title + "\n" + 
                           "GitHub Issue: " + githubUrl;

        // 3. Send to Slack
        slackNotificationPort.postMessage(channel, slackBody);
    }
}
