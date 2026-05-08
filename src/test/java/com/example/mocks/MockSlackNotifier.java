package com.example.mocks;

import com.example.ports.SlackNotifierPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifierPort for testing.
 * Captures messages sent during workflow execution.
 */
public class MockSlackNotifier implements SlackNotifierPort {

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
    public void sendMessage(String channel, String body) {
        // In a real mock, we might just capture. Here we capture and can verify later.
        this.messages.add(new SentMessage(channel, body));
    }

    public List<SentMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }
}
