package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Records calls to verify interactions.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class Call {
        public final String channel;
        public final String body;

        public Call(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<Call> calls = new ArrayList<>();
    private boolean shouldSucceed = true;

    @Override
    public boolean postMessage(String channel, String body) {
        calls.add(new Call(channel, body));
        return shouldSucceed;
    }

    public List<Call> getCalls() {
        return calls;
    }

    public void setShouldSucceed(boolean succeed) {
        this.shouldSucceed = succeed;
    }

    public void clear() {
        calls.clear();
    }
}
