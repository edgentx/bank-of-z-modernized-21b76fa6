package com.example.adapters;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack Notifications.
 * Allows tests to verify that a notification was "sent" and inspect the body.
 */
@Component
public class InMemorySlackNotificationAdapter implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendNotification(String channel, String body) {
        // Simulate sending by storing in memory
        System.out.println("[Mock Slack] Sending to " + channel + ": " + body);
        this.sentMessages.add(body);
    }

    /**
     * Helper method for tests to retrieve the last sent body.
     */
    public String getLastMessageBody() {
        if (sentMessages.isEmpty()) {
            return null;
        }
        return sentMessages.get(sentMessages.size() - 1);
    }

    public void clear() {
        sentMessages.clear();
    }
}
