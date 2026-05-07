package com.example.service;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Service to handle defect reporting commands.
 * Orchestrates GitHub issue creation and Slack notifications.
 */
public class DefectReportService {

    private final GitHubPort githubPort;
    private final SlackPort slackPort;

    public DefectReportService(GitHubPort githubPort, SlackPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    public void execute(Command cmd) {
        if (cmd instanceof ReportDefectCmd c) {
            reportDefect(c);
        } else {
            throw new UnknownCommandException(cmd);
        }
    }

    private void reportDefect(ReportectCmd c) {
        // 1. Create the body for the GitHub issue
        String formattedDate = DateTimeFormatter.ISO_INSTANT.format(c.reportedAt());
        String issueBody = String.format(
            "Severity: %s\nComponent: %s\nReported At: %s",
            c.severity(),
            c.component(),
            formattedDate
        );

        // 2. Create issue in GitHub
        String issueUrl = githubPort.createIssue(c.title(), issueBody);

        // 3. Verify URL was returned
        if (issueUrl == null || issueUrl.isBlank()) {
            throw new IllegalStateException("GitHub URL was not returned by adapter");
        }

        // 4. Send notification to Slack with the GitHub URL
        String slackMessage = String.format(
            "Defect Reported: %s\nGitHub issue: %s",
            c.title(),
            issueUrl
        );

        slackPort.sendMessage("#vforce360-issues", slackMessage);
    }
}