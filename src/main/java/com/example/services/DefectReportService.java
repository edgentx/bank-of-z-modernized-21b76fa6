package com.example.services;

import com.example.adapters.GitHubPort;
import com.example.adapters.SlackPort;
import com.example.domain.defect.model.ReportDefectCmd;
import org.springframework.stereotype.Service;

/**
 * Service handling the reporting workflow (Temporal equivalent).
 * Orchestrates the creation of a GitHub issue and the subsequent notification via Slack.
 */
@Service
public class DefectReportService {

    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    public DefectReportService(GitHubPort gitHubPort, SlackPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    /**
     * Handles the report defect command.
     * 1. Creates an issue in GitHub.
     * 2. Posts a message to Slack with the GitHub URL included.
     *
     * @param cmd The command containing defect details.
     */
    public void handleReportDefect(ReportDefectCmd cmd) {
        // 1. Create GitHub Issue
        // We construct the body from the description or use a template if description is null
        String issueBody = cmd.description() != null ? cmd.description() : "Defect reported via VForce360.";
        String githubUrl = gitHubPort.createIssue(cmd.title(), issueBody);

        // 2. Post to Slack
        // The requirement is that the Slack body MUST include the GitHub URL.
        // We append the URL to the message content to ensure visibility.
        String slackMessage = String.format(
            "Defect Reported: %s\nSeverity: %s\nComponent: %s\nGitHub Issue: %s",
            cmd.title(),
            cmd.severity(),
            cmd.component(),
            githubUrl
        );

        slackPort.postMessage("#vforce360-issues", slackMessage);
    }
}