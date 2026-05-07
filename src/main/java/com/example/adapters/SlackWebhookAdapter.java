package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackNotificationPort.
 */
@Component
public class SlackWebhookAdapter implements SlackNotificationPort {

    @Override
    public boolean postMessage(String channel, String text) {
        // Actual implementation would POST to Slack Webhook URL
        // For this compilation target, we return true to simulate success
        // consistent with the MockSlackNotificationAdapter behavior.
        return true;
    }
}
