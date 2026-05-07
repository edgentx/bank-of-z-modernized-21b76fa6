package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import com.example.domain.vforce.model.DefectReportedEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Tracks messages sent without calling the real API.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class SentMessage {
        public final String channel;
        public final String body;
        public final String githubUrl; // Extracted for easy verification

        public SentMessage(String channel, String body, String githubUrl) {
            this.channel = channel;
            this.body = body;
            this.githubUrl = githubUrl;
        }
    }

    private final List<SentMessage> sentMessages = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public void sendDefectAlert(String channel, DefectReportedEvent event) {
        if (shouldFail) {
            throw new RuntimeException("Mock Slack API Failure");
        }

        // Simulate constructing the body based on the event
        // This mirrors the expected real adapter behavior
        String body = String.format(
            "[%s] %s\nSeverity: %s\nDetails: %s\nGitHub Issue: %s",
            event.getAggregateId(),
            event.getTitle(),
            event.getSeverity(),
            event.getDescription(),
            event.getGitHubUrl() // CRITICAL: Verify this is present
        );

        sentMessages.add(new SentMessage(channel, body, event.getGitHubUrl()));
    }

    public List<SentMessage> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    public void setShouldFail(boolean flag) {
        this.shouldFail = flag;
    }
}
