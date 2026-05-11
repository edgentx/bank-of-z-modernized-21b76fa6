package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify contents without real I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class SentMessage {
        public final String channel;
        public final String body;

        public SentMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<SentMessage> messages = new ArrayList<>();
    private boolean simulateFailure = false;

    @Override
    public boolean send(String channel, String body) {
        if (simulateFailure) {
            return false;
        }
        messages.add(new SentMessage(channel, body));
        return true;
    }

    public List<SentMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
}
