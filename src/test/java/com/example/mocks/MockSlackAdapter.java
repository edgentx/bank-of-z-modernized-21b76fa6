package com.example.mocks;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MockSlackAdapter implements SlackPort {

    public final List<Message> messages = new ArrayList<>();

    @Override
    public boolean postMessage(String channelId, String text) {
        messages.add(new Message(channelId, text));
        return true;
    }

    public record Message(String channelId, String text) {}
}