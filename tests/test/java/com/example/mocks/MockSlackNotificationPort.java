package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures calls to verify content without real I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<CapturedCall> calls = new ArrayList<>();

    @Override
    public void postMessage(String channel, String body) {
        // Store the call details for assertion
        calls.add(new CapturedCall(channel, body));
    }

    public boolean wasCalled() {
        return !calls.isEmpty();
    }

    public String getLastMessageBody() {
        if (calls.isEmpty()) {
            return null;
        }
        return calls.get(calls.size() - 1).body();
    }

    public String getLastChannel() {
        if (calls.isEmpty()) {
            return null;
        }
        return calls.get(calls.size() - 1).channel();
    }

    public void reset() {
        calls.clear();
    }

    private record CapturedCall(String channel, String body) {}
}
