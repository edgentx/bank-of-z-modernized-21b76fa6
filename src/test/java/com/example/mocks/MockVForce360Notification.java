package com.example.mocks;

import com.example.ports.VForce360NotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for VForce360 Slack notifications.
 * Stores the last received body to verify formatting (e.g., GitHub URL presence).
 */
public class MockVForce360Notification implements VForce360NotificationPort {

    public static class SentMessage {
        public final String defectId;
        public final String url;

        public SentMessage(String defectId, String url) {
            this.defectId = defectId;
            this.url = url;
        }
    }

    private final List<SentMessage> messages = new ArrayList<>();

    @Override
    public boolean sendDefectSlack(String defectId, String issueUrl) {
        // Simulate basic validation
        if (defectId == null || issueUrl == null) return false;
        if (defectId.isEmpty() || issueUrl.isEmpty()) return false;
        
        this.messages.add(new SentMessage(defectId, issueUrl));
        return true;
    }

    public List<SentMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }
}