package com.example.application;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.ValidationAggregate;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Application service handling the defect reporting workflow.
 * Orchestrates Aggregate execution, GitHub creation, and Slack notification.
 */
@Service
public class DefectReportingService {

    private final GitHubPort githubPort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(GitHubPort githubPort,
                                  SlackNotificationPort slackNotificationPort) {
        this.githubPort = githubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Handles the reporting of a defect.
     * 1. Executes command on Aggregate.
     * 2. Creates GitHub Issue.
     * 3. Sends Slack notification with the GitHub URL.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        // 1. Execute Domain Logic
        ValidationAggregate aggregate = new ValidationAggregate(cmd.validationId());
        var events = aggregate.execute(cmd);

        // We expect a single event for this command
        if (events.isEmpty()) {
            throw new IllegalStateException("Expected a DefectReportedEvent");
        }

        // Safe cast as we control the aggregate logic
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);

        // 2. Create GitHub Issue
        // Using description for body, summary for title
        String issueUrl = githubPort.createIssue(
            "Defect: " + event.description().substring(0, Math.min(50, event.description().length())),
            event.description()
        );

        // 3. Notify Slack (VW-454: Body must include GitHub URL)
        String slackMessage = String.format(
            "Defect Reported by %s (Severity: %s):\n%s\nGitHub Issue: %s",
            event.reporter(),
            event.severity(),
            event.description(),
            issueUrl
        );

        slackNotificationPort.postMessage("#vforce360-issues", slackMessage);
    }
}
