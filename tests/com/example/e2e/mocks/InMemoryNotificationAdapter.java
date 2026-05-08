package com.example.e2e.mocks;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.ports.VForce360NotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for VForce360NotificationPort.
 * Stores notification bodies in memory for verification during testing.
 * (Replaces real Slack HTTP Client)
 */
public class InMemoryNotificationAdapter implements VForce360NotificationPort {

    private final List<String> postedBodies = new ArrayList<>();

    @Override
    public void postDefectNotification(DefectReportedEvent event) {
        // Simulate the formatting logic that would happen in the real adapter or worker
        // This acts as the 'System Under Test' for the formatting aspect initially.
        String body = formatMessage(event);
        postedBodies.add(body);
    }

    private String formatMessage(DefectReportedEvent event) {
        // This is a stub implementation that might be missing the logic (Reproducing the bug)
        // or it might be correct.
        return String.format(
                "Defect Reported: %s\nDescription: %s", // Intentionally missing URL for negative test
                event.title(),
                event.description()
                // event.githubIssueUrl() // Logic is missing here in the mock/implementation
        );
    }

    public String getLastPostedBody() {
        if (postedBodies.isEmpty()) return null;
        return postedBodies.get(postedBodies.size() - 1);
    }

    public void clear() {
        postedBodies.clear();
    }
}