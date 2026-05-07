package com.example.mocks;

import com.example.ports.SlackNotifier;

/**
 * Mock adapter for SlackNotifier.
 * Used in testing to verify that messages are generated correctly
 * without performing actual network I/O to Slack.
 */
public class MockSlackNotifier implements SlackNotifier {

    private String lastMessageBody;
    private boolean sendCalled = false;

    @Override
    public void send(SlackMessage message) {
        this.sendCalled = true;
        this.lastMessageBody = message.body();
        // No real HTTP call
    }

    public boolean wasSendCalled() {
        return sendCalled;
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }

    public void reset() {
        this.lastMessageBody = null;
        this.sendCalled = false;
    }
}
