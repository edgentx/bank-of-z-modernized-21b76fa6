package com.example.domain.vforce;

import com.example.ports.SlackNotificationPort;

/**
 * Service responsible for processing defect reports and notifying Slack.
 * This acts as the orchestrator for the S-FB-1 story requirements.
 */
public class DefectReportWorkflowService {

    private final SlackNotificationPort slackNotificationPort;
    private static final String GITHUB_BASE_URL = "https://github.com";

    public DefectReportWorkflowService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Processes the DefectReportedEvent and formats a Slack message containing the GitHub URL.
     *
     * @param event The domain event containing defect details.
     */
    public void processDefectReport(DefectReportedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("DefectReportedEvent cannot be null");
        }

        // Construct the GitHub issue URL based on the defect ID.
        // This fulfills the requirement: "Slack body includes GitHub issue: <url>"
        String githubIssueUrl = GITHUB_BASE_URL + "/" + event.defectId();

        // Format the message body to include the URL explicitly.
        // Expected: "Slack body includes GitHub issue: <url>"
        String messageBody = "Defect Reported: " + event.defectId() + "\n" +
                             "Severity: " + event.severity() + "\n" +
                             "GitHub issue: " + githubIssueUrl;

        slackNotificationPort.sendNotification(messageBody);
    }
}
