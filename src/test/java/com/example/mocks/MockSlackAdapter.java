package com.example.mocks;

import com.example.ports.SlackPort;

/**
 * Mock adapter for Slack.
 * Stores the last posted body to allow assertions in tests.
 */
public class MockSlackAdapter implements SlackPort {

    private String lastChannel;
    private String lastBody;

    @Override
    public void postMessage(String channel, String body) {
        this.lastChannel = channel;
        this.lastBody = body;
        // In a real mock, we might throw exceptions if certain inputs are detected
        // to simulate error conditions.
    }

    public String getLastPostedBody() {
        return lastBody;
    }

    public String getLastChannel() {
        return lastChannel;
    }
}
