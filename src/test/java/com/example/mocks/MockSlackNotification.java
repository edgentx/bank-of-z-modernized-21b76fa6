package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Stores captured messages to verify content.
 */
public class MockSlackNotification implements SlackNotificationPort {
    public String lastChannel;
    public String lastBody;
    public final List<String> allBodies = new ArrayList<>();

    @Override
    public void postMessage(String channel, String body) {
        this.lastChannel = channel;
        this.lastBody = body;
        this.allBodies.add(body);
    }

    public void reset() {
        lastChannel = null;
        lastBody = null;
        allBodies.clear();
    }
}