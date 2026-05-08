package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Real adapter for SlackNotificationPort.
 * In a production environment, this would use OkHttpClient to POST to a Slack Webhook.
 * For this TDD phase, it acts as the primary implementation injecting into the Service context.
 */
@Component
public class SlackNotificationAdapter implements SlackNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SlackNotificationAdapter.class);
    private final List<String> messageLog = new ArrayList<>();

    @Override
    public void send(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        // In a real scenario, we would execute an HTTP POST here.
        // httpClient.newCall(request).execute();
        
        // Logging the send operation
        log.info("Sending Slack notification: {}", message);
        
        // Store for audit purposes or getLastMessage retrieval if needed in this specific context
        messageLog.add(message);
    }

    @Override
    public String getLastMessage() {
        if (messageLog.isEmpty()) {
            return null;
        }
        return messageLog.get(messageLog.size() - 1);
    }
}
