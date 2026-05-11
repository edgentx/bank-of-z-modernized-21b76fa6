package com.example.domain.vforce;

import com.example.adapters.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Records messages locally instead of calling the real Slack API.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> capturedBodies = new ArrayList<>();
    private final List<String> capturedChannels = new ArrayList<>();

    @Override
    public void sendNotification(String channel, String body) {
        this.capturedChannels.add(channel);
        this.capturedBodies.add(body);
        // System.out.println("[MockSlack] Sent to " + channel + ": " + body);
    }

    public List<String> getCapturedBodies() {
        return capturedBodies;
    }

    public List<String> getCapturedChannels() {
        return capturedChannels;
    }

    public void clear() {
        capturedBodies.clear();
        capturedChannels.clear();
    }
}
