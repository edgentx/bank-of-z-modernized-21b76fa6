package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack for assertion.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class SentMessage {
        public final String projectId;
        public final String message;

        public SentMessage(String projectId, String message) {
            this.projectId = projectId;
            this.message = message;
        }
    }

    private final List<SentMessage> messages = new ArrayList<>();

    @Override
    public void sendDefectNotification(String projectId, String message) {
        this.messages.add(new SentMessage(projectId, message));
    }

    public List<SentMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }

    public boolean hasReceivedMessageContaining(String substring) {
        return messages.stream().anyMatch(m -> m.message.contains(substring));
    }
}
