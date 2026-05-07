package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages sent to Slack.
 */
public class MockSlackAdapter implements SlackPort {

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
    public void sendMessage(String channel, String text) {
        this.messages.add(new Message(channel, text));
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public Message getLastMessage() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }

    public void reset() {
        messages.clear();
    }
}
