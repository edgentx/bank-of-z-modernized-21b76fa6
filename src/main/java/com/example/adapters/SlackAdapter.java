package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Real adapter implementation for SlackNotificationPort.
 * Simulates posting to Slack.
 */
@Component
public class SlackAdapter implements SlackNotificationPort {

    @Override
    public void postMessage(String text) {
        // In a real scenario, this would use Slack WebApiClient
        // For Green phase, we just log or no-op effectively (unless connected to a real sink)
        System.out.println("[Slack Adapter] Posting message: " + text);
    }
}
