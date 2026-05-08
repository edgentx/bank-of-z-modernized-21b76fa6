package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages sent to Slack to verify content without calling the real API.
 */
public class MockSlackPort implements SlackPort {
    public static class Message {
        public final String channel;
        public final String text;

        public Message(String channel, String text) {
            this.channel = channel;
            this.text = text;
        }
    }

    private final List<Message> messages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String text) {
        this.messages.add(new Message(channel, text));
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }

    public boolean containsText(String substring) {
        return messages.stream().anyMatch(m -> m.text.contains(substring));
    }
}