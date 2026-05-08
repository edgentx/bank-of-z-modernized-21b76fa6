package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Records sent messages to verify content and recipients.
 */
public class MockSlackPort implements SlackPort {

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
    public boolean sendMessage(String channel, String messageBody) {
        messages.add(new SentMessage(channel, messageBody));
        return true;
    }

    public List<SentMessage> getMessages() {
        return new ArrayList<>(messages);
    }
}