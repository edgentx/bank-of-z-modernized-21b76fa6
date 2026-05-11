package com.example.mocks;

import com.example.domain.vforce360.model.DefectReportedEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of a Notification Service.
 * Used in tests to capture outbound Slack messages without real I/O.
 */
public class MockNotificationService {

    private final List<String> sentMessages = new ArrayList<>();

    public void sendSlackNotification(String message) {
        sentMessages.add(message);
    }

    public boolean wasSlackMessageSentContaining(String text) {
        return sentMessages.stream().anyMatch(msg -> msg.contains(text));
    }

    public void reset() {
        sentMessages.clear();
    }
}
