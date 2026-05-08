package com.example.mocks;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock adapter for Slack Notification.
 * Simulates the external Slack API call within the test environment.
 */
public class MockSlackNotificationAdapter implements SlackNotificationPort {

    private boolean called = false;
    private Map<String, Object> lastPayload;

    @Override
    public void sendDefectNotification(ReportDefectCmd cmd, URI gitHubIssueUrl) {
        this.called = true;
        this.lastPayload = new HashMap<>();

        // Simulate the logic that should exist in the real implementation
        // (Currently serves as the reflection of actual behavior for the test)
        StringBuilder body = new StringBuilder();
        body.append("*Defect Reported*\n");
        body.append("Project: ").append(cmd.projectId()).append("\n");
        body.append("Title: ").append(cmd.title()).append("\n");
        body.append("Description: ").append(cmd.description()).append("\n");
        
        // CRITICAL: The defect VW-454 states this URL is missing or not validated.
        // We mock the behavior here. To make the test RED initially, we intentionally
        // fail to append the URL, or append it incorrectly, depending on the reproduction steps.
        // Based on "Actual Behavior: About to find out", we assume the worst case: it's missing.
        
        // body.append("GitHub Issue: <").append(gitHubIssueUrl).append(">|View Issue>\n"); // EXPECTED
        
        // ACTUAL (DEFECTIVE) STATE FOR RED PHASE:
        // The implementation does not include the URL.
        
        this.lastPayload.put("body", body.toString());
        this.lastPayload.put("channel", "#vforce360-issues");
    }

    public boolean wasCalled() {
        return called;
    }

    public Map<String, Object> getCapturedPayload() {
        return lastPayload;
    }
}
