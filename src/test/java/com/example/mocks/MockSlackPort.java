package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages to verify body content and URL presence.
 */
public class MockSlackPort implements SlackPort {

    public static class SentMessage {
        public final String channel;
        public final String text;
        public final List<MessageField> fields;

        public SentMessage(String channel, String text, List<MessageField> fields) {
            this.channel = channel;
            this.text = text;
            this.fields = fields;
        }
    }

    private final List<SentMessage> messages = new ArrayList<>();

    @Override
    public void sendMessage(String channel, String text, List<MessageField> fields) {
        this.messages.add(new SentMessage(channel, text, fields));
    }

    public List<SentMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }
}