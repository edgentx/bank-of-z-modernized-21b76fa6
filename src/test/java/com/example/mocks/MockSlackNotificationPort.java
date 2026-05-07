package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationPort implements SlackNotificationPort {

    private final List<String> sentMessages = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public boolean send(String payload) {
        if (shouldFail) return false;
        sentMessages.add(payload);
        return true;
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}
