package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

public class MockSlackPort implements SlackPort {
    public static class SentMessage {
        public final String channel;
        public final String message;

        public SentMessage(String channel, String message) {
            this.channel = channel;
            this.message = message;
        }
    }

    private final List<SentMessage> messages = new ArrayList<>();

    @Override
    public void sendMessage(String channel, String message) {
        messages.add(new SentMessage(channel, message));
    }

    public List<SentMessage> getMessages() {
        return messages;
    }

    public boolean containsLink(String link) {
        return messages.stream().anyMatch(m -> m.message.contains(link));
    }

    public void reset() {
        messages.clear();
    }
}
