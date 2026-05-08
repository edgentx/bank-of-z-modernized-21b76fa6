package com.example.mocks;

import com.example.domain.ports.SlackNotifier;

/**
 * Mock adapter for SlackNotifier.
 * Allows tests to inspect the body content that was "sent".
 */
public class SlackSpy implements SlackNotifier {

    private String capturedBody;
    private boolean sent = false;

    @Override
    public void sendNotification(String messageBody) {
        this.capturedBody = messageBody;
        this.sent = true;
        // No real I/O performed
    }

    public boolean wasNotificationSent() {
        return sent;
    }

    public String getCapturedBody() {
        return capturedBody;
    }
}
