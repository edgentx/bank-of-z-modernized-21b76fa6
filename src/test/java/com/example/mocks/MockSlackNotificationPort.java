package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages posted to Slack to verify content without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class PostedMessage {
        public final String channel;
        public final String body;

        public PostedMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<PostedMessage> postedMessages = new ArrayList<>();
    private boolean shouldSucceed = true;

    @Override
    public boolean postMessage(String channel, String body) {
        postedMessages.add(new PostedMessage(channel, body));
        return shouldSucceed;
    }

    public List<PostedMessage> getPostedMessages() {
        return new ArrayList<>(postedMessages);
    }

    public void reset() {
        postedMessages.clear();
        shouldSucceed = true;
    }

    public void setShouldSucceed(boolean succeed) {
        this.shouldSucceed = succeed;
    }
}
