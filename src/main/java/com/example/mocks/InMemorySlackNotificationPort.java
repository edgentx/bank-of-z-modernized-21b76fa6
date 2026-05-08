package com.example.mocks;

import com.example.services.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class InMemorySlackNotificationPort implements SlackNotificationPort {
    public final List<String> messages = new ArrayList<>();

    @Override
    public void send(String message) {
        messages.add(message);
    }
    
    public boolean containsUrl(String url) {
        return messages.stream().anyMatch(msg -> msg.contains(url));
    }
}