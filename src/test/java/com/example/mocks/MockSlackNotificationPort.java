package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationPort implements SlackNotificationPort {
    public final List<String> postedMessages = new ArrayList<>();

    @Override
    public void postMessage(String text) {
        // In a real scenario, we might throw if text is null,
        // but for verification we just record it.
        this.postedMessages.add(text);
    }

    public String getLastMessage() {
        if (postedMessages.isEmpty()) return null;
        return postedMessages.get(postedMessages.size() - 1);
    }

    public void clear() {
        postedMessages.clear();
    }
}
