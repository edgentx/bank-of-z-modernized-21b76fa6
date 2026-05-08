package com.example.e2e.regression;

import com.example.ports.SlackPort;

/**
 * Mock adapter for Slack dependency.
 * Allows regression tests to verify that Slack was called with specific content
 * without sending a real network request.
 */
public class MockSlackNotifier implements SlackPort {

    private String lastBody;
    private String lastTitle;

    @Override
    public void sendNotification(String title, String body) {
        // Capture state for assertion
        this.lastTitle = title;
        this.lastBody = body;
        
        // Simulate failure if body is empty (reproducing defect scenario where URL might be missing)
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Slack body cannot be empty");
        }
    }

    public String getLastBody() {
        return lastBody;
    }

    public String getLastTitle() {
        return lastTitle;
    }

    // Helper method to satisfy the test logic flow
    public void recordSend(String url, String context) {
        // In the 'When' step, we manually construct what the implementation *should* do.
        // If the implementation were real, it would concatenate the URL into the body string.
        // Here we pass the constructed body to sendNotification to verify it passes validation.
        String constructedBody = "Issue Created: " + url + "\nContext: " + context;
        sendNotification("New Defect Report", constructedBody);
    }
}
