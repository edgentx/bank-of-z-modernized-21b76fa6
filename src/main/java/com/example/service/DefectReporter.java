package com.example.service;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Service responsible for reporting defects to external monitoring systems (Slack).
 * 
 * Context: VW-454 Validation
 * The defect report indicated that the GitHub URL was missing from the Slack body.
 * This implementation ensures the URL is explicitly formatted and included.
 */
@Component
public class DefectReporter {

    private final SlackNotificationPort slackNotificationPort;

    /**
     * Constructs the DefectReporter with a specific notification port.
     * 
     * @param slackNotificationPort The port adapter for Slack communication.
     */
    public DefectReporter(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect to the #vforce360-issues channel.
     * 
     * @param defectId The ID of the defect (e.g., "VW-454").
     * @param githubUrl The direct URL to the GitHub issue.
     * @return true if the report was successfully sent, false otherwise.
     */
    public boolean reportDefect(String defectId, String githubUrl) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or empty");
        }
        if (githubUrl == null || githubUrl.isBlank()) {
            throw new IllegalArgumentException("githubUrl cannot be null or empty");
        }

        // Fix for VW-454: Explicitly format the body to ensure the URL is present.
        // The defect was caused by a missing URL in the notification body.
        String channel = "#vforce360-issues";
        String body = String.format("Issue reported: %s. Link: %s", defectId, githubUrl);

        return slackNotificationPort.sendMessage(channel, body);
    }
}
