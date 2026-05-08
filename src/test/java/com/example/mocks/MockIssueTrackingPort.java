package com.example.mocks;

import com.example.ports.IssueTrackingPort;

/**
 * Mock implementation of IssueTrackingPort for testing.
 * Simulates GitHub responses.
 */
public class MockIssueTrackingPort implements IssueTrackingPort {

    private String simulatedBaseUrl = "http://github.example.com/mock-repo/issues/";
    private int issueCounter = 1;

    @Override
    public String createIssue(String title, String description) {
        // Simulate the creation of an issue and return a deterministic URL
        return simulatedBaseUrl + issueCounter++;
    }

    public void setSimulatedBaseUrl(String url) {
        this.simulatedBaseUrl = url;
    }
}
