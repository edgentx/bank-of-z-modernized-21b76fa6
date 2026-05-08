package com.example.mocks;

import com.example.ports.SlackPort;

public class MockSlackPort implements SlackPort {
    private String lastMessage;

    public String getLastMessage() {
        return lastMessage;
    }

    @Override
    public void sendMessage(String message) {
        this.lastMessage = message;
    }
}
