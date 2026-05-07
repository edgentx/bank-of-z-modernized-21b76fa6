package com.example.mocks;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.ports.VForce360NotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of VForce360NotificationPort for testing.
 * Captures published events to allow assertions on the Slack body content.
 */
public class MockVForce360NotificationPort implements VForce360NotificationPort {

    private final List<DefectReportedEvent> publishedEvents = new ArrayList<>();

    @Override
    public void publishDefect(DefectReportedEvent event) {
        // Simulate the Temporal/Slack workflow step
        this.publishedEvents.add(event);
    }

    public List<DefectReportedEvent> getPublishedEvents() {
        return new ArrayList<>(publishedEvents);
    }

    public void clear() {
        publishedEvents.clear();
    }

    /**
     * Helper method to validate VW-454:
     * Verifies that the latest published event contains the GitHub URL in the context
     * (simulating the verification of the Slack body).
     */
    public boolean verifyLatestEventContainsGitHubUrl() {
        if (publishedEvents.isEmpty()) return false;
        DefectReportedEvent latest = publishedEvents.get(publishedEvents.size() - 1);
        String url = latest.githubIssueUrl();
        
        // Valid URLs must not be null/blank and start with http
        return url != null && !url.isBlank() && url.startsWith("http");
    }
}
