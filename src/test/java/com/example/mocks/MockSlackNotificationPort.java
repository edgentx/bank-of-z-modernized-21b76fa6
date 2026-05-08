package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify content without real I/O.
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

    @Override
    public void send(String channel, String body) {
        System.out.println("[MockSlack] Sending to " + channel + ": " + body);
        this.messages.add(new SentMessage(channel, body));
    }

    public List<SentMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }

    /**
     * Helper to check if any captured message body contains the specific text.
     */
    public boolean bodyContains(String text) {
        return messages.stream().anyMatch(m -> m.body.contains(text));
    }
}
