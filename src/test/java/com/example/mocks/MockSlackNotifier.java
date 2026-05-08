package com.example.mocks;

import com.example.ports.SlackNotifierPort;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock adapter for Slack notifications.
 * Verifies that messages were sent and captures their content.
 */
public class MockSlackNotifier implements SlackNotifierPort {

    public final List<Message> messages = new ArrayList<>();

    public record Message(String channel, String body) {}

    @Override
    public void postMessage(String channel, String messageBody) {
        // System.out.println("[MockSlack] Sending to " + channel + ": " + messageBody);
        messages.add(new Message(channel, messageBody));
    }

    public void reset() {
        messages.clear();
    }
}
