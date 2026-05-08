package com.example.domain.report;

import com.example.domain.ports.GitHubIssuePort;
import com.example.domain.ports.SlackNotificationPort;
import com.example.domain.report.model.DefectReportedEvent;
import com.example.domain.report.model.ReportDefectCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Application Service for handling defect reporting workflows.
 * Orchestrates the creation of GitHub issues and Slack notifications.
 */
public class DefectReportService {
    private static final Logger log = LoggerFactory.getLogger(DefectReportService.class);

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportService(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the ReportDefectCommand.
     * 1. Creates a GitHub issue.
     * 2. Sends a Slack notification containing the GitHub URL.
     * 3. Returns the resulting domain event.
     */
    public CompletableFuture<List<Object>> handleReportDefect(ReportDefectCommand cmd) {
        log.info("Reporting defect: {}", cmd.defectId());

        return gitHubIssuePort.createIssue(cmd.title(), cmd.description())
                .thenCompose(githubUrl -> {
                    // Validate VW-454: Ensure GitHub URL is present
                    if (githubUrl == null || githubUrl.isBlank()) {
                        throw new IllegalStateException("GitHub URL must not be empty");
                    }

                    // Expected Behavior: Slack body includes GitHub issue URL
                    String slackBody = String.format(
                            "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
                            cmd.title(), cmd.severity(), githubUrl
                    );

                    return slackNotificationPort.send(slackBody)
                            .thenApply(slackTs -> {
                                DefectReportedEvent event = new DefectReportedEvent(
                                        cmd.defectId(),
                                        cmd.title(),
                                        cmd.severity(),
                                        githubUrl,
                                        Instant.now()
                                );
                                // Return list of side effects for testing/validation
                                return List.of(event, slackBody);
                            });
                });
    }
}