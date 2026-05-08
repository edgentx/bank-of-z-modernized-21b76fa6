package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify content and URLs.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<String> postedMessages = new ArrayList<>();

    @Override
    public void postMessage(String messageBody) {
        this.postedMessages.add(messageBody);
    }

    public String getLastMessage() {
        if (postedMessages.isEmpty()) return null;
        return postedMessages.get(postedMessages.size() - 1);
    }

    public void clear() {
        postedMessages.clear();
    }
}
