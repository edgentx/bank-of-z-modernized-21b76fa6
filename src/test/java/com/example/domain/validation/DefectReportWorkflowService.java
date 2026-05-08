package com.example.domain.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service implementation for reporting defects.
 * Orchestrates GitHub issue creation and Slack notification.
 */
@Service
public class DefectReportWorkflowService {

    private final GitHubPort githubPort;
    private final SlackNotificationPort slackPort;
    private static final String DEFAULT_CHANNEL = "#vforce360-issues";

    public DefectReportWorkflowService(GitHubPort githubPort, SlackNotificationPort slackPort) {
        this.githubPort = githubPort;
        this.slackPort = slackPort;
    }

    /**
     * Reports a defect to VForce360.
     * 1. Creates GitHub Issue.
     * 2. Notifies Slack with the issue URL.
     *
     * @param title The defect title.
     * @param description The defect description.
     */
    public void reportDefect(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Defect title cannot be null or blank");
        }

        // 1. Create Issue in GitHub
        String issueUrl = githubPort.createIssue(title, description);

        // Validate workflow state
        if (issueUrl == null || issueUrl.isBlank()) {
            throw new IllegalStateException("GitHub URL generation failed: URL was null");
        }

        // 2. Notify Slack with the URL
        String slackBody = "New Defect Reported: " + issueUrl;
        boolean sent = slackPort.sendMessage(DEFAULT_CHANNEL, slackBody);

        if (!sent) {
            // Depending on requirements, we might throw here or log.
            // For VW-454 validation, the primary check is the URL presence in the body passed to the port.
            // The mock adapter returns true, so this path is for real adapter failures.
            throw new RuntimeException("Failed to send notification to Slack channel: " + DEFAULT_CHANNEL);
        }
    }
}
