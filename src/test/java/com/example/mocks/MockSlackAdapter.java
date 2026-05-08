package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of SlackPort for testing.
 * Captures payloads in memory to verify content without calling the real API.
 */
public class MockSlackAdapter implements SlackPort {

    private Map<String, String> latestPayload;
    private boolean notificationSent = false;
    private String lastChannel;

    @Override
    public void sendNotification(String channel, String body, Map<String, String> contextMap) {
        this.lastChannel = channel;
        this.notificationSent = true;
        
        // Capture data for assertions
        this.latestPayload = new HashMap<>();
        this.latestPayload.put("channel", channel);
        this.latestPayload.put("body", body);
        if (contextMap != null) {
            this.latestPayload.putAll(contextMap);
        }
    }

    // Test Utility Methods
    
    public boolean wasNotificationSent() {
        return notificationSent;
    }

    public Map<String, String> getLatestPayload() {
        return latestPayload;
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public void reset() {
        this.latestPayload = null;
        this.notificationSent = false;
        this.lastChannel = null;
    }
}
