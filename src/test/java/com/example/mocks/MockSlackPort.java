package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackPort implements SlackPort {
    public final List<Message> messages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String body) {
        messages.add(new Message(channel, body));
    }

    public record Message(String channel, String body) {}

    public boolean receivedMessageContaining(String text) {
        return messages.stream().anyMatch(m -> m.body().contains(text));
    }
}
