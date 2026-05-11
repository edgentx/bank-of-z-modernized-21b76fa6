package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackPort implements SlackPort {
    public final List<String> messages = new ArrayList<>();
    public String lastChannel;
    public String lastBody;

    @Override
    public void sendSlackMessage(String channel, String body) {
        this.lastChannel = channel;
        this.lastBody = body;
        this.messages.add(body);
    }

    public void reset() {
        messages.clear();
        lastChannel = null;
        lastBody = null;
    }
}
