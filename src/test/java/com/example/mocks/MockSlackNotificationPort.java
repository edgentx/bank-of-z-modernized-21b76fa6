package com.example.mocks;

import com.example.domain.vforce.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotificationPort implements SlackNotificationPort {
    public final List<String> postedMessages = new ArrayList<>();
    public String lastChannel;

    @Override
    public void postMessage(String channel, String text) {
        this.lastChannel = channel;
        this.postedMessages.add(text);
    }
}