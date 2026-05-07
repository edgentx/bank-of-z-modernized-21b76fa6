package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Allows inspection of the messages that would have been sent.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    // Record of invocations
    public static class Call {
        public final String channelId;
        public final String messageBody;

        public Call(String channelId, String messageBody) {
            this.channelId = channelId;
            this.messageBody = messageBody;
        }
    }

    private final List<Call> calls = new ArrayList<>();

    @Override
    public boolean sendMessage(String channelId, String messageBody) {
        // In a real mock, we might allow configuring return values,
        // but for Slack we usually just want to verify the call happened.
        this.calls.add(new Call(channelId, messageBody));
        return true;
    }

    public List<Call> getCalls() {
        return List.copyOf(calls);
    }

    public void reset() {
        calls.clear();
    }
}