package com.example.domain.vforce360.service;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Service for reporting defects.
 * Driven by Story S-FB-1: Validating VW-454.
 */
@Service
public class DefectReportService {

    private final SlackNotificationPort slackNotificationPort;
    private static final String DEFAULT_CHANNEL_ID = "C0123456";

    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = Objects.requireNonNull(slackNotificationPort, "SlackNotificationPort is required");
    }

    /**
     * Reports a defect to the VForce360 Slack channel.
     * Validates that the GitHub URL is present and correctly formatted.
     *
     * @param defectId  The ID of the defect (e.g., "VW-454")
     * @param githubUrl The full URL to the GitHub issue
     * @throws IllegalArgumentException if githubUrl is null or blank
     */
    public void reportDefect(String defectId, String githubUrl) {
        if (githubUrl == null || githubUrl.isBlank()) {
            throw new IllegalArgumentException(
                String.format("GitHub URL is required for defect report: %s", defectId)
            );
        }

        // Construct the message body strictly as expected by the regression test
        String messageBody = String.format(
            "Defect Detected: %s\nGitHub Issue: %s",
            defectId,
            githubUrl
        );

        slackNotificationPort.postMessage(DEFAULT_CHANNEL_ID, messageBody);
    }
}
