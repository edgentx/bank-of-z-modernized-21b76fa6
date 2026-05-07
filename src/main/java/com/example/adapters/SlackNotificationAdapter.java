package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Real-world adapter for sending Slack notifications.
 * Implements the SlackNotificationPort interface.
 * In a production environment, this would use the Slack Web API.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    @Override
    public void postMessage(String channelId, String messageBody) {
        // DEV NOTE: Actual implementation logic not required for TDD green phase.
        // The test suite mocks the port interface to verify integration logic.
        // Real implementation would invoke WebClient.post() to Slack API.
    }
}