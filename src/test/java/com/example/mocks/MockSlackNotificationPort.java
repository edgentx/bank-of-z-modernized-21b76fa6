package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack for assertion.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public String lastChannel;
    public String lastMessageBody;
    public final List<String> allMessageBodies = new ArrayList<>();

    @Override
    public void postMessage(String channel, String messageBody) {
        this.lastChannel = channel;
        this.lastMessageBody = messageBody;
        this.allMessageBodies.add(messageBody);
    }

    public void reset() {
        lastChannel = null;
        lastMessageBody = null;
        allMessageBodies.clear();
    }
}
