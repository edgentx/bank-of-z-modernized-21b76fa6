package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Captures messages sent to Slack for verification in tests.
 */
public class MockSlackPort implements SlackPort {

    private boolean notifyCalled;
    private String lastChannel;
    private String lastMessageBody;
    private final List<String> callLog = new ArrayList<>();

    @Override
    public void sendNotification(String channel, String messageBody) {
        this.notifyCalled = true;
        this.lastChannel = channel;
        this.lastMessageBody = messageBody;
        this.callLog.add("sendNotification to " + channel);
    }

    // --- Test Helper Methods ---

    public boolean wasNotifyCalled() {
        return notifyCalled;
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }

    public void reset() {
        this.notifyCalled = false;
        this.lastChannel = null;
        this.lastMessageBody = null;
        this.callLog.clear();
    }
}
