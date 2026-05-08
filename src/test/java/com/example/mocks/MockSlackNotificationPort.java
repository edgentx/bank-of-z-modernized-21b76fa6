package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory Mock for Slack Port.
 * Captures messages sent to Slack for verification in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public record SlackMessage(String channel, String body) {}

    private final List<SlackMessage> postedMessages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String body) {
        postedMessages.add(new SlackMessage(channel, body));
    }

    @Override
    public void validateAndPost(String channel, String body) {
        // This is the RED phase logic.
        // If the actual implementation doesn't do this, we might not catch it here,
        // but the assertions in the test will check if the URL was present in the body sent.
        postMessage(channel, body);
    }

    public List<SlackMessage> getPostedMessages() {
        return postedMessages;
    }

    public void clear() {
        postedMessages.clear();
    }
}