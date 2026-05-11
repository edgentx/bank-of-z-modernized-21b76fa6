package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Captures messages to memory so assertions can be verified.
 */
public class MockSlackPort implements SlackPort {
    
    public static class PostedMessage {
        public final String channel;
        public final String text;
        
        public PostedMessage(String channel, String text) {
            this.channel = channel;
            this.text = text;
        }
    }
    
    private final List<PostedMessage> messages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String text) {
        messages.add(new PostedMessage(channel, text));
    }
    
    public List<PostedMessage> getMessages() {
        return new ArrayList<>(messages);
    }
    
    public void clear() {
        messages.clear();
    }
}
