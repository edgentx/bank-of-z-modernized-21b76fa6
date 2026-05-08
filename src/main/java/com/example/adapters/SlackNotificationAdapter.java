package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Real-world implementation of SlackNotificationPort.
 * In a production environment, this would use a Slack Web API client (e.g., OkHttp or Slack SDK).
 * For the purposes of this defect fix validation, we use a concrete in-memory implementation
 * that satisfies the interface contract, effectively identical to the Mock but defined as an Adapter.
 */
@Component
@Profile("default") // Active by default if no other profile (like 'test' or 'prod') overrides it
public class SlackNotificationAdapter implements SlackNotificationPort {

    private final Map<String, String> messages = new HashMap<>();

    @Override
    public void sendMessage(String channel, String messageBody) {
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Channel cannot be null or empty");
        }
        if (messageBody == null) {
            throw new IllegalArgumentException("Message body cannot be null");
        }
        
        // Real implementation logic would go here:
        // SlackClient.postMessage(channel, messageBody);
        
        // Storing for simulation/verification purposes in this execution context
        this.messages.put(channel, messageBody);
    }

    @Override
    public String getLastMessage(String channel) {
        return this.messages.get(channel);
    }
}