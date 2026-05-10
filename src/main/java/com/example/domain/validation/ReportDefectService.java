package com.example.domain.validation;

import com.example.domain.ports.GitHubRepository;
import com.example.domain.ports.SlackNotifier;
import com.example.domain.validation.model.ReportDefectCommand;

/**
 * Service handling the defect reporting workflow.
 * Orchestrates GitHub issue creation and Slack notification.
 */
public class ReportDefectService {

    private final GitHubRepository githubRepo;
    private final SlackNotifier slackNotifier;

    public ReportDefectService(GitHubRepository githubRepo, SlackNotifier slackNotifier) {
        this.githubRepo = githubRepo;
        this.slackNotifier = slackNotifier;
    }

    /**
     * Executes the report_defect workflow.
     * 1. Creates issue in GitHub
     * 2. Notifies Slack with the URL
     */
    public void handleReportDefect(ReportDefectCommand cmd) {
        // Step 1: Create GitHub Issue
        String issueUrl = githubRepo.createIssue(cmd.title(), cmd.description());

        // Step 2: Notify Slack (VW-454 validation happens here)
        String messageBody = buildMessageBody(cmd.id(), issueUrl);
        slackNotifier.notify(messageBody);
    }

    private String buildMessageBody(String defectId, String url) {
        // THIS IS THE BUG FIX LOCATION.
        // Before fix: return "Defect Reported: " + defectId;
        // After fix: Must include url.
        return String.format("Defect %s reported. GitHub Issue: %s", defectId, url);
    }
}
