package com.example.mocks;

import com.example.domain.defect.model.DefectReportedEvent;
import com.example.ports.SlackNotifierPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for SlackNotifierPort.
 * Stores captured events to allow assertions in tests.
 */
public class MockSlackNotifier implements SlackNotifierPort {

    private final List<DefectReportedEvent> capturedEvents = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public void notify(DefectReportedEvent event) {
        if (shouldFail) {
            throw new RuntimeException("Simulated Slack API failure");
        }
        capturedEvents.add(event);
    }

    public List<DefectReportedEvent> getCapturedEvents() {
        return capturedEvents;
    }

    public void reset() {
        capturedEvents.clear();
        shouldFail = false;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    /**
     * Helper to verify VW-454: Check if the last notification contained the GitHub URL.
     */
    public boolean lastNotificationContainsGithubUrl() {
        if (capturedEvents.isEmpty()) return false;
        DefectReportedEvent last = capturedEvents.get(capturedEvents.size() - 1);
        return last.githubIssueUrl() != null && !last.githubIssueUrl().isBlank();
    }
}
