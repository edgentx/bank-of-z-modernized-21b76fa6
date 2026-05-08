package com.example.domain.reporting.service;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service responsible for orchestrating the reporting of defects.
 * This implements the logic required by the S-FB-1 Story / VW-454 validation.
 */
@Service
public class DefectReportingService {

    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(GitHubPort gitHubPort, SlackNotificationPort slackNotificationPort) {
        this.gitHubPort = gitHubPort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Executes the defect reporting workflow:
     * 1. Generate the GitHub URL for the defect ID.
     * 2. Format the Slack message body.
     * 3. Post the notification to the default channel.
     *
     * @param defectId The ID of the defect (e.g., "VW-454").
     */
    public void reportDefect(String defectId) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("Defect ID cannot be null or empty");
        }

        // 1. Generate URL
        String url = gitHubPort.generateIssueUrl(defectId);

        // 2. Format Message
        // Strict adherence to expected format: "GitHub issue: <url>"
        String messageBody = "Defect reported. GitHub issue: " + url;

        // 3. Send to Slack
        slackNotificationPort.postToDefaultChannel(messageBody);
    }
}