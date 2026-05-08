package com.example.domain.defect.service;

import com.example.domain.defect.model.ReportDefectCommand;
import com.example.domain.defect.ports.SlackNotificationPort;

/**
 * Service handling the logic for reporting defects (part of the Temporal workflow).
 * In a real scenario, this would be orchestrated by Temporal activities.
 */
public class DefectReportingService {

    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports the defect to the external monitoring system (Slack).
     * Formats the message body to include the defect details and the GitHub issue URL.
     *
     * @param cmd The command containing defect details.
     */
    public void reportDefect(ReportDefectCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("ReportDefectCommand cannot be null");
        }

        // Format the GitHub URL as a Slack link (<url|text>) or simply (<url>) for auto-linking.
        // Based on VW-454 test expectation: we expect the raw URL enclosed in angle brackets.
        String formattedGithubUrl = (cmd.githubUrl() != null) ? "<" + cmd.githubUrl() + ">" : "No GitHub URL provided";

        // Construct the message body
        String body = String.format(
            "*New Defect Reported*\n" +
            "*Project ID:* %s\n" +
            "*Title:* %s\n" +
            "*Description:* %s\n" +
            "*GitHub Issue:* %s",
            cmd.projectId(),
            cmd.title(),
            cmd.description(),
            formattedGithubUrl
        );

        // Delegate to the port (adapter)
        slackNotificationPort.send(body);
    }
}
