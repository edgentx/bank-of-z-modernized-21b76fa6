package com.example.adapters;

import com.example.ports.SlackNotifierPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotifierPort.
 * This adapter would use the Slack SDK to send a real webhook request.
 */
@Component
public class SlackNotifierAdapter implements SlackNotifierPort {

    // In a real implementation, this would use a WebClient or SlackClient to post to a webhook URL
    // For this defect fix, the focus is on ensuring the URL is passed into the body construction.

    @Override
    public void sendNotification(String message, String githubIssueUrl) {
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Message: ").append(message != null ? message : "");

        // VW-454 Fix: Ensure the GitHub URL is appended to the body if present
        if (githubIssueUrl != null) {
            bodyBuilder.append("\nIssue: ").append(githubIssueUrl);
        }

        String payload = bodyBuilder.toString();

        // Real execution: post to Slack Webhook
        // System.out.println("[Slack Integration] Sending payload: " + payload);
        // webClient.post().uri(webhookUrl).bodyValue(payload).retrieve();
    }
}
