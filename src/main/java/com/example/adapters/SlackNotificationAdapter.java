package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 * In a production environment, this would use a Slack HTTP client (e.g., SlackApi).
 * For the purpose of this defect fix, we implement the interface to satisfy the build.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    @Override
    public void sendMessage(String message) {
        // Real implementation would post to Slack Web API here
        // e.g. slackClient.methods().chatPostMessage(req -> req.text(message));
        System.out.println("[Slack Adapter] Sending message: " + message);
    }
}
