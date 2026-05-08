package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack in memory to verify content.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<PostedMessage> postedMessages = new ArrayList<>();

    public record PostedMessage(String channel, String messageBody) {}

    @Override
    public void postMessage(String channel, String messageBody) {
        this.postedMessages.add(new PostedMessage(channel, messageBody));
    }

    public List<PostedMessage> getPostedMessages() {
        return new ArrayList<>(postedMessages);
    }

    public void clear() {
        postedMessages.clear();
    }

    /**
     * Finds the first message posted to the specific channel.
     */
    public PostedMessage findMessageByChannel(String targetChannel) {
        return postedMessages.stream()
            .filter(m -> m.channel().equals(targetChannel))
            .findFirst()
            .orElse(null);
    }
}
