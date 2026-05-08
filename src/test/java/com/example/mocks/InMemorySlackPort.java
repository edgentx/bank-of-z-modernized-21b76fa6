package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages in memory to verify output without real I/O.
 */
public class InMemorySlackPort implements SlackPort {

    public static class SlackMessage {
        public final String channel;
        public final String body;

        public SlackMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<SlackMessage> postedMessages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String body) {
        // In a real test, we might assert here. 
        // In this mock, we capture to allow flexible assertions later.
        this.postedMessages.add(new SlackMessage(channel, body));
    }

    public List<SlackMessage> getPostedMessages() {
        return postedMessages;
    }

    public void clear() {
        postedMessages.clear();
    }
}
