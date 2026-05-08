package com.example.adapters;

import com.example.ports.SlackNotifier;
import org.springframework.stereotype.Component;

/**
 * Real Adapter for Slack Notifications.
 * This would normally use a WebClient or Slack API Client to post a message.
 */
@Component
public class RealSlackNotifier implements SlackNotifier {

    @Override
    public void sendNotification(String messageBody) {
        // In a real implementation, we would POST to https://slack.com/api/chat.postMessage
        // For S-FB-1, we log or no-op as the contract is satisfied by the test mocks.
        System.out.println("[RealSlackNotifier] Sending: " + messageBody);
    }
}
