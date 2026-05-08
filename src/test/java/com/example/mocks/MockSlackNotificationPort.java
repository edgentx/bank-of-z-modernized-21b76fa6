package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationPort implements SlackNotificationPort {
    public final List<Message> messages = new ArrayList<>();

    @Override
    public void sendMessage(String channel, String message) {
        this.messages.add(new Message(channel, message));
    }

    public record Message(String channel, String content) {}
}
