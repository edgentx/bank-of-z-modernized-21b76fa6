package com.example.mocks;

import com.example.ports.SlackNotifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotifier that captures messages for verification.
 */
public class CapturingSlackNotifier implements SlackNotifier {
    public static class Notification {
        public final String aggregateId;
        public final String githubUrl;

        public Notification(String aggregateId, String githubUrl) {
            this.aggregateId = aggregateId;
            this.githubUrl = githubUrl;
        }
    }

    private final List<Notification> capturedNotifications = new ArrayList<>();

    @Override
    public void notifyDefectReported(String aggregateId, String githubIssueUrl) {
        capturedNotifications.add(new Notification(aggregateId, githubIssueUrl));
    }

    public List<Notification> getCapturedNotifications() {
        return capturedNotifications;
    }

    public void clear() {
        capturedNotifications.clear();
    }
}
