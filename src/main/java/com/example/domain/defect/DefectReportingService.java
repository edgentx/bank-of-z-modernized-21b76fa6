package com.example.domain.defect;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Domain Service for handling defect reporting logic.
 * Orchestrates the retrieval of GitHub URLs and the composition of Slack notifications.
 * <p>
 * Implements the logic required to fix defect VW-454.
 * </p>
 */
@Service
public class DefectReportingService {

    private final SlackNotificationPort slackPort;
    private final GitHubIssuePort githubPort;

    /**
     * Constructor injection for Ports.
     *
     * @param slackPort The Slack notification adapter.
     * @param githubPort The GitHub issue adapter.
     */
    public DefectReportingService(SlackNotificationPort slackPort, GitHubIssuePort githubPort) {
        this.slackPort = slackPort;
        this.githubPort = githubPort;
    }

    /**
     * Reports a defect by generating a notification and sending it to Slack.
     * <p>
     * This method is the entry point for the Temporal workflow activity.
     * It ensures that the GitHub URL is included in the Slack body if available.
     * </p>
     *
     * @param defectId The unique identifier of the defect (e.g., "VW-454").
     * @throws IllegalArgumentException if defectId is null or empty.
     */
    public void reportDefect(String defectId) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or empty");
        }

        // 1. Retrieve the URL from the GitHub Port
        Optional<String> potentialUrl = githubPort.getIssueUrl(defectId);

        // 2. Compose the payload
        String payload;
        if (potentialUrl.isPresent()) {
            // This block satisfies the Expected Behavior for VW-454.
            // It explicitly appends the URL to the message body.
            payload = String.format("Defect Reported: %s\nGitHub Issue: %s", defectId, potentialUrl.get());
        } else {
            // Handle the edge case where GitHub does not return a URL.
            // This satisfies the Regression Edge Case test.
            payload = String.format("Defect Reported: %s", defectId);
        }

        // 3. Send notification via Slack Port
        slackPort.send(payload);
    }
}
