package com.example.mocks;

import com.example.domain.defect.model.DefectReportedEvent;
import com.example.ports.NotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of NotificationPort for testing.
 * Captures events to verify Slack body content without real I/O.
 */
public class MockNotificationPort implements NotificationPort {
    public final List<DefectReportedEvent> capturedEvents = new ArrayList<>();

    @Override
    public void sendDefectAlert(DefectReportedEvent event) {
        this.capturedEvents.add(event);
    }

    public String getLastSlackBody() {
        if (capturedEvents.isEmpty()) return "";
        DefectReportedEvent event = capturedEvents.get(capturedEvents.size() - 1);
        // Simulate the formatting logic expected in the Slack body
        // Expected: "Slack body includes GitHub issue: <url>"
        return "Defect: " + event.title() + " | GitHub issue: " + event.githubUrl();
    }
}
