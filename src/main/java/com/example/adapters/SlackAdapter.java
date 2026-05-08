package com.example.adapters;

import com.example.ports.SlackPort;

/**
 * Real implementation of the SlackPort.
 * Constructs the Slack message body ensuring the GitHub URL is included.
 */
public class SlackAdapter implements SlackPort {

    private static final String GITHUB_BASE_URL = "https://github.com/bank-of-z/issues/";

    @Override
    public void sendDefectNotification(String defectId, String summary, String githubIssueId) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or empty");
        }
        if (githubIssueId == null || githubIssueId.isBlank()) {
            throw new IllegalArgumentException("githubIssueId cannot be null or empty");
        }

        // Fix for VW-454: Ensure the body contains the GitHub URL
        String url = GITHUB_BASE_URL + githubIssueId;
        String body = String.format(
            "Defect %s: %s\nSee issue: %s",
            defectId,
            summary != null ? summary : "No summary",
            url
        );

        // Simulate sending the message to the external API.
        // In a production system, this would use an HTTP client (e.g., WebClient, RestTemplate).
        System.out.println("Sending to Slack: " + body);

        // Note: The test suite mocks this interface, but if this adapter is used directly,
        // this is the logic that must execute.
    }
}
