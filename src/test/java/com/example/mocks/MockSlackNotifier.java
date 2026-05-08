package com.example.mocks;

import com.example.domain.slack.SlackMessage;
import com.example.ports.SlackNotifier;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotifier implements SlackNotifier {
    public final List<SlackMessage> sentMessages = new ArrayList<>();

    @Override
    public void send(SlackMessage message) {
        sentMessages.add(message);
    }

    public void clear() {
        sentMessages.clear();
    }

    public SlackMessage getFirst() {
        if (sentMessages.isEmpty()) return null;
        return sentMessages.get(0);
    }
}
