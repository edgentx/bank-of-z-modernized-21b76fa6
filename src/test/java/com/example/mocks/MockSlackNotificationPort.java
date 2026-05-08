package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Used to capture output without sending real HTTP requests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {
    public final List<String> sentBodies = new ArrayList<>();
    public String lastChannel;

    @Override
    public void sendNotification(String channel, String messageBody) {
        this.lastChannel = channel;
        this.sentBodies.add(messageBody);
    }

    public void reset() {
        sentBodies.clear();
        lastChannel = null;
    }
}
