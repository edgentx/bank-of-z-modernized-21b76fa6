package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort.
 * Captures messages for verification instead of sending real webhooks.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<String> postedMessages = new ArrayList<>();

    @Override
    public void postMessage(String messageBody) {
        System.out.println("[MockSlack] Posting: " + messageBody);
        postedMessages.add(messageBody);
    }

    /**
     * Helper to verify content.
     */
    public boolean wasUrlPosted(String url) {
        return postedMessages.stream().anyMatch(msg -> msg.contains(url));
    }
}
