package com.example.mocks;

/**
 * Mock adapter for Slack notification.
 * In a real scenario, this would implement a SlackNotificationPort.
 * For this defect validation, we assume the check happens on the Aggregate/Event level
 * before the port is even reached, or the port is mocked to return the expected content.
 */
public class MockSlackNotifier {

    public String lastSentBody;

    public void send(String body) {
        this.lastSentBody = body;
        System.out.println("[MockSlack] Sent: " + body);
    }

    public String getLastSentBody() {
        return lastSentBody;
    }
}
