package com.example.e2e.mocks;

import com.example.adapters.SlackPort;

/**
 * Mock implementation of SlackPort for testing.
 * Captures the message body sent to Slack to verify content.
 */
public class MockSlackAdapter implements SlackPort {

    private String capturedBody;
    private boolean messageSent;

    public void reset() {
        this.capturedBody = null;
        this.messageSent = false;
    }

    public boolean wasMessageSent() {
        return messageSent;
    }

    public String getCapturedBody() {
        return capturedBody;
    }

    @Override
    public void postMessage(String channel, String body) {
        this.messageSent = true;
        this.capturedBody = body;
    }
}
