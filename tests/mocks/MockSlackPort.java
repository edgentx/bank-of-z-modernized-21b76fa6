package com.example.mocks;

import com.example.ports.SlackPort;

public class MockSlackPort implements SlackPort {
    private String lastBody;
    private String lastChannel;

    @Override
    public void sendMessage(String channel, String body) {
        this.lastChannel = channel;
        this.lastBody = body;
        System.out.println("[MOCK SLACK] Sent to " + channel + ": " + body);
    }

    public String getLastBody() {
        return lastBody;
    }
}