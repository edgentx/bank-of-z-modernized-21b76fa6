package com.example.domain.validation;

import com.example.ports.SlackNotifier;

/**
 * Mock Adapter for SlackNotifier.
 * Captures the message body to verify the URL is present.
 */
public class MockSlackNotifier implements SlackNotifier {

    private String capturedBody;

    @Override
    public void sendNotification(String body) {
        // Capture the body for assertions in the test
        this.capturedBody = body;
    }

    public String getCapturedBody() {
        return capturedBody;
    }
}
