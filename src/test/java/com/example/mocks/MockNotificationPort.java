package com.example.mocks;

import com.example.ports.NotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockNotificationPort implements NotificationPort {
    public List<String> messages = new ArrayList<>();
    public String lastChannel;

    @Override
    public void send(String channel, String message) {
        System.out.println("[MOCK] Sending to " + channel + ": " + message);
        this.lastChannel = channel;
        this.messages.add(message);
    }

    public String getLastMessage() {
        return messages.isEmpty() ? null : messages.get(messages.size() - 1);
    }
}
