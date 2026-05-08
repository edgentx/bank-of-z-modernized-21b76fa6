package com.example.domain.notification;

import com.example.domain.shared.Command;
import com.example.ports.SlackPort;
import com.example.ports.GitHubPort;
import java.util.List;

/**
 * Service to orchestrate defect reporting.
 * This is the primary target for the defect fix VW-454.
 */
public class NotificationService {

    private final SlackPort slackPort;
    private final GitHubPort gitHubPort;

    public NotificationService(SlackPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Executes the ReportDefectCommand.
     * 1. Creates issue in GitHub.
     * 2. Posts notification to Slack including the GitHub URL.
     */
    public void handleReportDefect(ReportDefectCommand cmd) {
        // 1. Call GitHub to create issue
        String gitHubUrl = gitHubPort.createIssue(cmd.title(), cmd.description());

        // 2. Construct Slack message
        // DEFECT VW-454: Previously the Slack body did NOT contain the GitHub URL.
        // We must verify the URL is present.
        String slackMessage = String.format(
            "Defect Reported: %s\nDetails: %s\nGitHub Issue: %s",
            cmd.title(), cmd.description(), gitHubUrl
        );

        slackPort.sendMessage(slackMessage);
    }
}