package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Captures messages sent to Slack to verify content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void sendDefectReport(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return messages;
    }

    public void reset() {
        messages.clear();
    }

    public boolean containsUrl(String url) {
        return messages.stream().anyMatch(msg -> msg.contains(url));
    }
}
