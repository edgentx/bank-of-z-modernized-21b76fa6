package com.example.domain.validation.service;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;

/**
 * Service to handle defect reporting commands.
 * This file represents the skeleton/implementation context required to run the tests.
 */
public class DefectReportingService {

    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    public void handle(ReportDefectCmd cmd) {
        if (cmd == null || cmd.defectId() == null) {
            throw new IllegalArgumentException("Invalid ReportDefectCommand");
        }

        // STEP 1: Determine GitHub URL (Mocked logic for TDD setup)
        // In reality this might call a GitHub Port or construct a link based on config.
        String githubUrl = "https://github.com/org/repo/issues/" + cmd.defectId();

        // STEP 2: Construct the Slack Body
        // The defect implies the body might be missing the URL.
        // The fix ensures it is present.
        String slackBody = String.format(
            "Defect Reported: %s\nID: %s\nGitHub Issue: %s",
            cmd.title(),
            cmd.defectId(),
            githubUrl
        );

        // STEP 3: Send to Slack
        slackNotificationPort.postMessage("#vforce360-issues", slackBody);
    }
}
