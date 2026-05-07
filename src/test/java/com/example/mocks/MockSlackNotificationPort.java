package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures the last sent payload to allow verification in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private String lastChannel;
    private Map<String, Object> lastPayload;
    private boolean sendCalled = false;

    @Override
    public void sendNotification(String channel, Map<String, Object> payload) {
        this.lastChannel = channel;
        this.lastPayload = new HashMap<>(payload); // Defensive copy
        this.sendCalled = true;
    }

    public boolean isSendCalled() {
        return sendCalled;
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public Map<String, Object> getLastPayload() {
        return lastPayload;
    }

    public String getLastMessageBody() {
        if (lastPayload != null && lastPayload.containsKey("text")) {
            return lastPayload.get("text").toString();
        }
        return null;
    }

    public void reset() {
        this.lastChannel = null;
        this.lastPayload = null;
        this.sendCalled = false;
    }
}
