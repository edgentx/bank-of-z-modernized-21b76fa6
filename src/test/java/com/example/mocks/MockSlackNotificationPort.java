package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to verify formatting and content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public String lastChannel;
    public String lastBody;
    public final List<MessageRecord> history = new ArrayList<>();
    private boolean shouldSucceed = true;

    public record MessageRecord(String channel, String body) {}

    public void setShouldSucceed(boolean succeed) {
        this.shouldSucceed = succeed;
    }

    @Override
    public boolean postMessage(String channel, String body) {
        this.lastChannel = channel;
        this.lastBody = body;
        history.add(new MessageRecord(channel, body));
        return shouldSucceed;
    }

    public void clear() {
        lastChannel = null;
        lastBody = null;
        history.clear();
    }
}
