package com.example.mocks;

import com.example.ports.SlackNotifierPort;

/**
 * Spy/Mock Adapter for Slack Notifier.
 * Captures output for verification in tests.
 */
public class SpySlackNotifier implements SlackNotifierPort {

    private String lastChannel;
    private String lastMessageBody;
    private boolean called = false;

    @Override
    public void send(String channel, String messageBody) {
        // Capture the state
        this.lastChannel = channel;
        this.lastMessageBody = messageBody;
        this.called = true;
        
        // System.out.println("[MOCK SLACK] Sent to " + channel + ": " + messageBody);
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }

    public boolean wasCalled() {
        return called;
    }
}
