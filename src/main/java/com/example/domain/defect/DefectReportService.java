package com.example.domain.defect;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

/**
 * Domain Service responsible for handling defect reporting logic.
 * Orchestrates the creation of the defect report and ensures validation
 * requirements (such as GitHub URL presence) are met before notification.
 */
@Service
public class DefectReportService {

    private final SlackNotificationPort slackNotificationPort;

    public DefectReportService(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect and posts a notification to Slack.
     * This method serves as the implementation target for the VW-454 regression test.
     *
     * @param defectId The ID of the defect (e.g., "VW-454").
     * @param githubUrl The URL to the GitHub issue.
     */
    public void reportDefect(String defectId, String githubUrl) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or blank");
        }
        if (githubUrl == null || githubUrl.isBlank()) {
            throw new IllegalArgumentException("githubUrl cannot be null or blank");
        }

        // Construct the message body ensuring the URL is included as per acceptance criteria
        String body = String.format(
            "Defect Reported: %s%nDetails: See %s for reproduction.",
            defectId,
            githubUrl
        );

        // Post via the port
        this.slackNotificationPort.postMessage(body);
    }
}