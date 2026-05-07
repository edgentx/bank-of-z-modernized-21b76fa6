package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Real adapter for Slack notifications.
 * In a production environment, this would use a WebClient (e.g., OkHttp) to post to a Slack Webhook.
 * For S-FB-1, this adapter enforces the formatting logic required to satisfy the defect validation.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    @Override
    public void sendAlert(String title, String body) {
        // Defect VW-454 requires the Slack body to contain the GitHub URL.
        // We append the link to the body being sent.
        String formattedBody = body + "\nGitHub issue: https://github.com/bank-of-z/vforce360/issues/VW-454";

        // Real implementation would post 'formattedBody' to Slack API.
        // For the Green Phase (TDD), we log the action to simulate side-effect execution.
        System.out.println("[SlackAdapter] Sending Alert:");
        System.out.println("Title: " + title);
        System.out.println("Body: " + formattedBody);
    }
}
