package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock adapter for SlackNotificationPort.
 * Stores messages in memory instead of sending them to the real Slack API.
 */
public class InMemorySlackNotifier implements SlackNotificationPort {

    private final Map<String, String> channelMessages = new HashMap<>();

    @Override
    public String sendMessage(String channel, String messageBody) {
        // Store the message so we can verify it later in tests
        channelMessages.put(channel, messageBody);
        // Return a fake timestamp
        return "1234567890.123456";
    }

    /**
     * Helper method for assertions to retrieve the last message sent to a specific channel.
     *
     * @param channel The channel to check
     * @return The last message body sent to that channel, or null if none exists.
     */
    public String getLastMessageBody(String channel) {
        return channelMessages.get(channel);
    }
}