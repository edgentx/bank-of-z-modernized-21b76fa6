package com.example.domain.validation;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Service for handling defect reporting workflows.
 * Coordinates the creation and dispatch of defect notifications.
 */
@Service
public class DefectReportService {

    private final SlackNotificationPort slackNotificationPort;

    // Constructor injection
    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect to the configured Slack channel.
     * Generates the message body including the GitHub issue URL.
     *
     * @param defectId The ID of the defect (e.g., "VW-454")
     * @param channel The target Slack channel.
     */
    public void reportDefect(String defectId, String channel) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null/blank");
        }
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("channel cannot be null/blank");
        }

        // VW-454: Construct the GitHub URL based on the defect ID
        String issueNumber = extractIssueNumber(defectId);
        String githubUrl = "https://github.com/example/bank-modernization/issues/" + issueNumber;

        String messageBody = "Defect Reported: " + defectId + "\n" +
                "GitHub Issue: " + githubUrl;

        slackNotificationPort.sendNotification(channel, messageBody);
    }

    /**
     * Helper to extract the numeric part from a defect ID like "VW-454".
     */
    private String extractIssueNumber(String defectId) {
        if (defectId.contains("-")) {
            return defectId.substring(defectId.lastIndexOf('-') + 1);
        }
        return defectId;
    }
}
