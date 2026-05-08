package com.example.domain.defect;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Domain Service handling the logic for reporting defects.
 * This is the Green Phase implementation for S-FB-1.
 */
@Service
public class DefectReportService {

    private final SlackNotificationPort slackNotificationPort;

    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Generates the defect report body and sends it to Slack.
     * Corresponds to the Temporal Activity implementation.
     *
     * @param defectId      The ID of the defect (e.g., "VW-454")
     * @param gitHubUrl     The direct URL to the GitHub issue
     * @param projectContext The project UUID or context string
     */
    public void reportDefect(String defectId, String gitHubUrl, String projectContext) {
        String slackBody = generateSlackBody(defectId, gitHubUrl, projectContext);
        slackNotificationPort.sendNotification(slackBody);
    }

    /**
     * Formats the Slack message body including the GitHub link.
     * Mirrors the logic in the test class to ensure the contract is met.
     */
    private String generateSlackBody(String defectId, String gitHubUrl, String projectContext) {
        return "Defect Report: " + defectId + "\nProject: " + projectContext + "\nGitHub Issue: " + gitHubUrl;
    }
}
