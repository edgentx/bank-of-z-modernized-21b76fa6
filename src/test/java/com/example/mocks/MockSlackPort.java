package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackPort implements SlackPort {
    public List<String> channels = new ArrayList<>();
    public List<String> messages = new ArrayList<>();

    @Override
    public void sendMessage(String channel, String text) {
        channels.add(channel);
        messages.add(text);
    }

    public String getLastMessage() {
        return messages.get(messages.size() - 1);
    }
}