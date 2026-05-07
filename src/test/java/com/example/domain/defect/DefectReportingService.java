package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCommand;
import com.example.domain.defect.ports.GitHubIssueTracker;
import com.example.domain.defect.ports.NotificationService;

/**
 * Service to orchestrate defect reporting.
 * GREEN PHASE: This implementation now correctly integrates GitHub and Slack
 * to ensure the Slack body contains the GitHub issue URL.
 */
public class DefectReportingService {

    private final GitHubIssueTracker gitHub;
    private final NotificationService notificationService;

    public DefectReportingService(GitHubIssueTracker gitHub, NotificationService notificationService) {
        this.gitHub = gitHub;
        this.notificationService = notificationService;
    }

    public void processDefect(ReportDefectCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("ReportDefectCommand cannot be null");
        }
        if (cmd.title() == null || cmd.title().isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }

        // 1. Construct the GitHub issue body
        // We use String.format for cleaner string building.
        StringBuilder issueBody = new StringBuilder();
        issueBody.append(String.format("**Defect ID:** %s%n", cmd.defectId()));
        issueBody.append(String.format("**Severity:** %s%n", cmd.severity()));
        issueBody.append(String.format("**Component:** %s%n", cmd.component()));
        issueBody.append(String.format("**Description:**%n%s%n", cmd.description()));

        // 2. Create the issue in GitHub and retrieve the URL
        // The GitHub adapter handles the actual API call (or mock logic).
        String githubUrl = gitHub.createIssue(cmd.title(), issueBody.toString());

        // 3. Construct the Slack notification body
        // CRITICAL FIX for VW-454: Append the returned URL to the message.
        StringBuilder slackMessage = new StringBuilder();
        slackMessage.append("*Defect Reported:*").append("\n");
        slackMessage.append(String.format("- *Title:* %s%n", cmd.title()));
        slackMessage.append(String.format("- *ID:* %s%n", cmd.defectId()));
        slackMessage.append(String.format("- *Severity:* %s%n", cmd.severity()));
        slackMessage.append("\n");
        slackMessage.append("*GitHub Issue Created:* ").append(githubUrl).append("\n");

        // 4. Send the notification
        notificationService.sendDefectNotification(slackMessage.toString());
    }
}
