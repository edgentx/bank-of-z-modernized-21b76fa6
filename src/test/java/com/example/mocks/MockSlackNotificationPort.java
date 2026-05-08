package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Records calls instead of performing real HTTP I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class Call {
        public final String defectId;
        public final String message;
        public final String gitHubIssueUrl;

        public Call(String defectId, String message, String gitHubIssueUrl) {
            this.defectId = defectId;
            this.message = message;
            this.gitHubIssueUrl = gitHubIssueUrl;
        }
    }

    private final List<Call> calls = new ArrayList<>();

    @Override
    public void sendDefectReport(String defectId, String message, String gitHubIssueUrl) {
        // In a real test, we might assert that gitHubIssueUrl is contained in message here,
        // but we'll let the test assertions handle verification logic to keep the mock simple.
        calls.add(new Call(defectId, message, gitHubIssueUrl));
    }

    public List<Call> getCalls() {
        return new ArrayList<>(calls);
    }

    public void reset() {
        calls.clear();
    }
}
