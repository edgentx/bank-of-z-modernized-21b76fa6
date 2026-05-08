package com.example.mocks;

import com.example.ports.VForce360NotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of VForce360NotificationPort.
 * Captures messages for verification in tests.
 */
public class InMemoryVForce360NotificationPort implements VForce360NotificationPort {

    public static class SentMessage {
        public final String defectId;
        public final String body;

        public SentMessage(String defectId, String body) {
            this.defectId = defectId;
            this.body = body;
        }
    }

    private final List<SentMessage> messages = new ArrayList<>();

    @Override
    public void reportDefect(String defectId, String message) {
        this.messages.add(new SentMessage(defectId, message));
    }

    public List<SentMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }
}
