package com.example.domain.validation.service;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service to handle defect reporting logic.
 * Generates the Slack payload with the GitHub issue URL and triggers the notification.
 */
@Service
public class DefectReportService {

    private final SlackNotificationPort slackNotificationPort;
    private static final String GITHUB_BASE_URL = "https://github.com/example/issues/";

    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect via Temporal and notifies Slack.
     * Constructs the JSON payload containing the GitHub URL.
     *
     * @param cmd The defect report command.
     */
    public void report(ReportDefectCmd cmd) {
        if (cmd == null || cmd.defectId() == null) {
            throw new IllegalArgumentException("ReportDefectCmd and defectId must not be null");
        }

        String githubUrl = GITHUB_BASE_URL + cmd.defectId();
        String payload = String.format(
            "{\"text\": \"Defect Reported: %s - %s. GitHub issue: %s\"}",
            cmd.defectId(),
            cmd.title(),
            githubUrl
        );

        slackNotificationPort.send(payload);
    }
}
