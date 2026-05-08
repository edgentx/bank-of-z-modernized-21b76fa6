package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Records sent messages to allow assertions on their content.
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
    private boolean shouldFail = false;

    @Override
    public boolean sendMessage(String channel, String body) {
        if (shouldFail) {
            return false;
        }
        messages.add(new SentMessage(channel, body));
        return true;
    }

    public List<SentMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}