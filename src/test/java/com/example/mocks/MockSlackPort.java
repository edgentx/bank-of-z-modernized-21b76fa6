package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

/** Mock Slack Port for testing. */
public class MockSlackPort implements SlackPort {
    private final List<String> postedMessages = new ArrayList<>();

    @Override
    public String postMessage(String channel, String text) {
        postedMessages.add(text);
        return text;
    }

    public String getLastMessageBody() {
        if (postedMessages.isEmpty()) return null;
        return postedMessages.get(postedMessages.size() - 1);
    }

    public boolean containsLink(String url) {
        return postedMessages.stream().anyMatch(msg -> msg.contains(url));
    }
}
