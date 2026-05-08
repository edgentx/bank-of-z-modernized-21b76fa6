package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;
    import java.util.List;

    public class MockSlackPort implements SlackPort {
        public List<String> messages = new ArrayList<>();

        @Override
        public CompletableFuture<Void> sendMessage(String channel, String message) {
            messages.add(message);
            return CompletableFuture.completedFuture(null);
        }

        public boolean lastMessageContains(String text) {
            return !messages.isEmpty() && messages.get(messages.size() - 1).contains(text);
        }
    }
