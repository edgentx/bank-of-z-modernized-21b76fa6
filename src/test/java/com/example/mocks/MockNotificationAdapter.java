package com.example.mocks;

import com.example.ports.VForce360NotificationPort;

/**
 * Mock adapter for VForce360/Temporal notifications.
 * Used in testing to simulate Slack/GitHub interactions without real I/O.
 */
public class MockNotificationAdapter implements VForce360NotificationPort {

    private boolean called = false;
    private String expectedIssueUrl;
    private String lastPayload;

    public void setExpectedIssueUrl(String url) {
        this.expectedIssueUrl = url;
    }

    @Override
    public String reportDefectAndCreateIssue(String defectId, String title, String description) {
        this.called = true;
        
        // Simulate constructing the Slack body
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("*Defect Detected*\n");
        bodyBuilder.append("ID: ").append(defectId).append("\n");
        bodyBuilder.append("Title: ").append(title).append("\n");
        bodyBuilder.append("Description: ").append(description).append("\n");
        
        // Critical behavior: The mock simulates the system appending the GitHub URL
        if (expectedIssueUrl != null) {
            bodyBuilder.append("GitHub Issue: ").append(expectedIssueUrl);
        }
        
        this.lastPayload = bodyBuilder.toString();
        
        return this.expectedIssueUrl;
    }

    public boolean wasCalled() {
        return called;
    }

    public String getLastPayload() {
        return lastPayload;
    }
}
