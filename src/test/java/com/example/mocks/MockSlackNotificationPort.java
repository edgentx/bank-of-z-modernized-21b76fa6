package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

/**
 * Mock adapter for Slack operations.
 * In a real test scenario, this might be replaced by Mockito.mock(),
 * but as a Mock Adapter Pattern requested in the prompt, we can implement it.
 * However, to perform verification (assertions), we usually need to spy on this
 * or add verification state to it. For strict Mockito usage, we would mock the interface.
 * Here we provide the Spring Bean implementation.
 */
@Component
public class MockSlackNotificationPort implements SlackNotificationPort {

    public String lastPostedChannel;
    public String lastPostedBody;

    @Override
    public void postMessage(String channel, String body) {
        // Capture state for verification if not using pure Mockito mocks in the test.
        this.lastPostedChannel = channel;
        this.lastPostedBody = body;
        
        // System.out.println("[MockSlack] Posted to " + channel + ": " + body);
    }

    // Helper for assertions if needed
    public boolean wasUrlPosted(String expectedUrl) {
        return lastPostedBody != null && lastPostedBody.contains(expectedUrl);
    }
}
