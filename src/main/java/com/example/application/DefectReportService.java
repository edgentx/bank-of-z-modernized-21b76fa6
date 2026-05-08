package com.example.application;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Application Service handling the defect reporting workflow.
 * Orchestrates the generation of the GitHub URL and the notification to Slack.
 */
@Service
public class DefectReportService {

    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportService(GitHubIssuePort gitHubIssuePort, SlackNotificationPort slackNotificationPort) {
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect to Slack including the GitHub issue URL.
     * 
     * @param defectId The ID of the defect (e.g., VW-454)
     * @param description The description of the defect
     * @return The result of the Slack notification attempt
     */
    public SlackNotificationPort.SendResult reportDefect(String defectId, String description) {
        // Generate the GitHub URL using the provided port
        String issueUrl = gitHubIssuePort.generateIssueUrl(defectId);

        // Delegate the actual sending to the Slack port
        return slackNotificationPort.reportDefect(defectId, issueUrl);
    }
}
