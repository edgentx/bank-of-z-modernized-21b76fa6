package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<SlackMessage> capturedMessages = new ArrayList<>();
    private boolean simulateFailure = false;

    public record SlackMessage(String channel, String body) {}

    @Override
    public boolean sendMessage(String channel, String body) {
        if (simulateFailure) return false;
        capturedMessages.add(new SlackMessage(channel, body));
        return true;
    }

    public void reset() {
        capturedMessages.clear();
        simulateFailure = false;
    }

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }

    public String getLastBody() {
        if (capturedMessages.isEmpty()) return null;
        return capturedMessages.get(capturedMessages.size() - 1).body();
    }
}
