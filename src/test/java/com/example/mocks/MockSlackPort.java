package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackPort implements SlackPort {
    public final List<String> messages = new ArrayList<>();
    public String lastChannel;

    @Override
    public void sendMessage(String channel, String message) {
        this.lastChannel = channel;
        this.messages.add(message);
    }
}