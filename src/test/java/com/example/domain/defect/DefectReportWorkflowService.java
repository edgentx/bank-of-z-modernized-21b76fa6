package com.example.domain.defect;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;

/**
 * Service handling the "Report Defect" workflow logic.
 * This class is the System Under Test (SUT).
 * In TDD, this file will be empty or minimal initially, causing tests to fail (Red),
 * then filled in to make tests pass (Green).
 */
public class DefectReportWorkflowService {

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportWorkflowService(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    /**
     * Orchestrates the defect reporting workflow:
     * 1. Create GitHub Issue.
     * 2. Notify Slack with the issue URL.
     *
     * @param title The defect title
     * @param body The defect description
     */
    public void reportDefect(String title, String body) {
        if (title == null) {
            throw new IllegalArgumentException("Defect title cannot be null");
        }

        // 1. Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(title, body);

        // 2. Notify Slack with the issue URL embedded in the body
        String messageBody = "Defect Reported: " + title + "\nGitHub Issue: " + issueUrl;
        slackPort.sendMessage("#vforce360-issues", messageBody);
    }
}
