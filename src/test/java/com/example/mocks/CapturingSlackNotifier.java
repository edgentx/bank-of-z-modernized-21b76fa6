package com.example.mocks;

import com.example.ports.SlackNotifier;

/**
 * Mock implementation of SlackNotifier for testing.
 * Captures the message body in memory so tests can assert on its content.
 */
public class CapturingSlackNotifier implements SlackNotifier {

    private String capturedBody;

    @Override
    public void notify(String messageBody) {
        if (messageBody == null) {
            throw new IllegalArgumentException("Message body cannot be null");
        }
        this.capturedBody = messageBody;
    }

    /**
     * Returns the message body captured in the last call to notify().
     * Returns null if notify has not been called yet.
     */
    public String getCapturedBody() {
        return this.capturedBody;
    }
}
