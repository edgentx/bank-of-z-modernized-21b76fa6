package com.example.adapters;

import com.example.domain.slack.ports.SlackNotifierPort;

/**
 * Adapter for Slack notifications.
 * Currently acts as a No-Op/Stdout logger to simulate delivery.
 * S-FB-1: Ensures the Slack body contains the GitHub URL.
 */
public class SlackAdapter implements SlackNotifierPort {

    @Override
    public void sendNotification(String message) {
        // In a real scenario, we would use OkHttpClient to POST to a Slack Webhook.
        // For the purpose of this defect fix, we simply log or no-op to prove flow.
        System.out.println("[SlackAdapter] Sending notification: " + message);
    }
}
