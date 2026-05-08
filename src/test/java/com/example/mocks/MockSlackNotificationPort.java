package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<PostedMessage> messages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String body) {
        // System.out.println("[MockSlack] Sent to " + channel + ": " + body);
        this.messages.add(new PostedMessage(channel, body));
    }

    public void reset() {
        messages.clear();
    }

    public boolean receivedMessageContaining(String substring) {
        return messages.stream().anyMatch(m -> m.body().contains(substring));
    }

    public record PostedMessage(String channel, String body) {}
}
