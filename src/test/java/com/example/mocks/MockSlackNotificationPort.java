package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationPort implements SlackNotificationPort {
    public final List<String> messages = new ArrayList<>();
    public String lastChannel;

    @Override
    public void notify(String channel, String message) {
        this.lastChannel = channel;
        this.messages.add(message);
    }

    public boolean wasNotified(String expectedSubstring) {
        return messages.stream().anyMatch(m -> m.contains(expectedSubstring));
    }
}
