package com.example.domain.validation;

import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

/**
 * Service responsible for reporting defects to Slack.
 * This is the "Green" phase implementation.
 * 
 * It validates input, generates the GitHub URL using the port, and constructs
 * the Slack body ensuring the URL is present before posting.
 */
public class DefectReportingService {

    private final SlackNotificationPort slackPort;
    private final GitHubIssuePort gitHubPort;

    public DefectReportingService(SlackNotificationPort slackPort, GitHubIssuePort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Reports a defect to the specified Slack channel.
     * 
     * @param defectId The ID of the defect (e.g., "VW-454")
     * @param channel The Slack channel (e.g., "#vforce360-issues")
     * @throws IllegalArgumentException if defectId or channel is null/blank
     */
    public void reportDefect(String defectId, String channel) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or blank");
        }
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("channel cannot be null or blank");
        }

        // 1. Retrieve the GitHub URL from the port
        String url = gitHubPort.getIssueUrl(defectId);

        // 2. Construct the message body including the URL
        // We append a newline to ensure the link is clean in Slack formatting
        String body = "Defect reported: " + defectId + "\n" + url;

        // 3. Post the message via the port
        slackPort.postMessage(channel, body);
    }
}
