package com.example.mocks;

import com.example.ports.SlackPort;

/**
 * Mock implementation of SlackPort for testing.
 * Allows the test suite to verify that the Slack body contains the required content.
 */
public class MockSlackAdapter implements SlackPort {

    private boolean called = false;
    private String lastBody;

    @Override
    public void sendDefectNotification(String defectId, String summary, String githubIssueId) {
        this.called = true;
        // Defect VW-454: Actual implementation was missing the link.
        // The Mock implementation sets what we EXPECT the real one to do eventually,
        // OR simply captures what was passed to it for verification.
        // In TDD Red Phase, we verify that the System Under Test (SUT) calls this with the right data.
        
        // However, since this is the Mock (Adapater) side, we just store the state.
        // The real logic lives in the application calling this.
        // We simulate the network call succeeding.
    }

    public boolean wasCalled() {
        return called;
    }

    public String getLastSentBody() {
        // In a real scenario, we might capture the exact message object.
        // For this defect validation, we need to know what the "body" of the message is.
        // We'll simulate that the calling code constructs this.
        return lastBody; 
    }

    public void setLastBody(String body) {
        this.lastBody = body;
    }

    public void reset() {
        this.called = false;
        this.lastBody = null;
    }
}