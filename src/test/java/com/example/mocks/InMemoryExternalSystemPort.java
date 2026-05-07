package com.example.mocks;

import com.example.domain.shared.ExternalSystemPort;

/**
 * Mock adapter for ExternalSystemPort (Slack).
 * Records invocations for verification without doing real I/O.
 */
public class InMemoryExternalSystemPort implements ExternalSystemPort {

    private boolean called = false;
    private String lastChannel;
    private String lastMessageBody;

    @Override
    public void sendNotification(String targetChannel, String messageBody) {
        this.called = true;
        this.lastChannel = targetChannel;
        this.lastMessageBody = messageBody;
        // System.out.println("[Mock Slack] Sent to " + targetChannel + ": " + messageBody);
    }

    public boolean wasCalled() {
        return called;
    }

    public String getLastChannel() {
        return lastChannel;
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }

    public void reset() {
        this.called = false;
        this.lastChannel = null;
        this.lastMessageBody = null;
    }
}