package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort.
 * Captures messages sent to Slack for assertion in tests.
 */
public class MockSlackNotification implements SlackNotificationPort {

    public static class SentMessage {
        public final String projectId;
        public final String defectId;
        public final String summary;
        public final String description;

        public SentMessage(String projectId, String defectId, String summary, String description) {
            this.projectId = projectId;
            this.defectId = defectId;
            this.summary = summary;
            this.description = description;
        }
    }

    private final List<SentMessage> messages = new ArrayList<>();

    @Override
    public void sendDefectReport(String projectId, String defectId, String summary, String description) {
        messages.add(new SentMessage(projectId, defectId, summary, description));
    }

    public List<SentMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }
}
