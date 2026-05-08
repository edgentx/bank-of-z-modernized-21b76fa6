package com.example.domain.report_defect.port;

/**
 * Mock Adapter for Slack Notifications.
 * Captures the message body for verification in tests.
 */
public class MockSlackPort implements SlackNotificationPort {

    private String lastPostedBody;
    private String lastChannel;

    public void reset() {
        this.lastPostedBody = null;
        this.lastChannel = null;
    }

    public String getLastPostedBody() {
        return lastPostedBody;
    }

    public String getLastChannel() {
        return lastChannel;
    }

    @Override
    public void sendNotification(String channel, String messageBody) {
        // Simulate network latency or queueing if necessary
        this.lastChannel = channel;
        this.lastPostedBody = messageBody;
        System.out.println("[MockSlack] Sent to " + channel + ": " + messageBody);
    }
}
