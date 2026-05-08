package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages in memory to verify content without external I/O.
 */
public class MockSlackPort implements SlackPort {

    public static class SlackMessage {
        public final String channelId;
        public final String body;

        public SlackMessage(String channelId, String body) {
            this.channelId = channelId;
            this.body = body;
        }
    }

    private final List<SlackMessage> sentMessages = new ArrayList<>();

    @Override
    public void sendMessage(String channelId, String messageBody) {
        this.sentMessages.add(new SlackMessage(channelId, messageBody));
    }

    public List<SlackMessage> getSentMessages() {
        return sentMessages;
    }

    public void clear() {
        sentMessages.clear();
    }
}
