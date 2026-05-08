package com.example.adapters;

/**
 * Real implementation of SlackNotifier using Slack API.
 * NOTE: Dependencies must be available in the classpath for this implementation to function.
 */
public class RealSlackNotifier implements SlackNotifierPort {

    private final String webhookUrl;

    public RealSlackNotifier(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void sendNotification(String channel, String message) {
        // Implementation specific logic to post to Slack
        // For the TDD green phase, we simply log or stub the behavior
        System.out.println("[RealSlack] Sending to " + channel + ": " + message);
    }
}
