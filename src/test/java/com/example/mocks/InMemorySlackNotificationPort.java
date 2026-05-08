package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages posted to verify content without calling the real API.
 */
public class InMemorySlackNotificationPort implements SlackNotificationPort {

    public String lastChannel;
    public String lastBody;
    public Map<String, String> lastContext = new HashMap<>();
    public boolean shouldReturnTrue = true;

    @Override
    public boolean postMessage(String channel, String body, Map<String, String> contextMetadata) {
        this.lastChannel = channel;
        this.lastBody = body;
        if (contextMetadata != null) {
            this.lastContext = contextMetadata;
        }
        return shouldReturnTrue;
    }

    public void reset() {
        lastChannel = null;
        lastBody = null;
        lastContext.clear();
    }
}
