package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackPort implements SlackPort {
    public final List<String> messages = new ArrayList<>();
    public String lastChannel;

    @Override
    public void postMessage(String channel, String text) {
        this.lastChannel = channel;
        this.messages.add(text);
    }

    public boolean containsUrl(String url) {
        return messages.stream().anyMatch(msg -> msg.contains(url));
    }

    public void clear() {
        messages.clear();
        lastChannel = null;
    }
}
