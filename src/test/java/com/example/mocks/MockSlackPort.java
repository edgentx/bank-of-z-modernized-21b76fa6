package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock for SlackPort to capture messages for assertion.
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
    public void sendMessage(String channel, String text) {
        messages.add(new Message(channel, text));
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void reset() {
        messages.clear();
    }
}
