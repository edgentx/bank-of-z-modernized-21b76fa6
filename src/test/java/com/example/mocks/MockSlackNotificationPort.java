package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to "Slack" for assertion in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class SlackMessage {
        public final String channel;
        public final String body;

        public SlackMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<SlackMessage> messages = new ArrayList<>();
    private boolean shouldFail = false;

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    @Override
    public boolean postMessage(String channel, String body) {
        if (shouldFail) {
            return false;
        }
        messages.add(new SlackMessage(channel, body));
        return true;
    }

    public List<SlackMessage> getMessages() {
        return messages;
    }

    public boolean lastMessageContains(String text) {
        if (messages.isEmpty()) return false;
        return messages.get(messages.size() - 1).body.contains(text);
    }

    public void reset() {
        messages.clear();
        shouldFail = false;
    }
}
