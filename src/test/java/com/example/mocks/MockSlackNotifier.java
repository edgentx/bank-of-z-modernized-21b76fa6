package com.example.mocks;

import com.example.ports.SlackNotifier;

/**
 * Mock Adapter for Slack Notifications.
 * Captures messages sent to Slack for assertion.
 */
public class MockSlackNotifier implements SlackNotifier {

    private String lastBody;
    private boolean notifyCalled = false;

    @Override
    public void sendNotification(String messageBody) {
        this.notifyCalled = true;
        this.lastBody = messageBody;
        // System.out.println("[MockSlack] Received: " + messageBody); // Debug helper
    }

    public boolean wasNotifyCalled() {
        return notifyCalled;
    }

    public String getLastBody() {
        return lastBody;
    }
}
