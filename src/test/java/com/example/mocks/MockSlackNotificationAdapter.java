package com.example.mocks;

/**
 * In-memory mock implementation of the Slack port.
 */
public class MockSlackNotificationAdapter implements MockSlackNotificationPort {
    private String lastBody;

    @Override
    public void sendNotification(String channel, String body) {
        this.lastBody = body;
        // System.out.println("[MockSlack] Sent to " + channel + ": " + body);
    }

    @Override
    public boolean lastBodyContains(String text) {
        if (lastBody == null) return false;
        return lastBody.contains(text);
    }
}