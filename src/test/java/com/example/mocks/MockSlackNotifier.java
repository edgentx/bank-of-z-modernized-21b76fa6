package com.example.mocks;

import com.example.ports.SlackNotifierPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifierPort for testing.
 * Captures messages to verify content without calling the real API.
 */
public class MockSlackNotifier implements SlackNotifierPort {

    public static class PostedMessage {
        public final String channel;
        public final String body;

        public PostedMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<PostedMessage> postedMessages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String messageBody) {
        this.postedMessages.add(new PostedMessage(channel, messageBody));
    }

    public List<PostedMessage> getPostedMessages() {
        return postedMessages;
    }

    public void reset() {
        postedMessages.clear();
    }

    public boolean lastMessageContains(String text) {
        if (postedMessages.isEmpty()) return false;
        return postedMessages.get(postedMessages.size() - 1).body.contains(text);
    }
}
