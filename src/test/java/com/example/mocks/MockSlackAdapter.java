package com.example.mocks;

import com.example.domain.shared.Command;
import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort.
 * Allows inspection of payloads sent during tests without real I/O.
 */
public class MockSlackAdapter implements SlackNotificationPort {

    private final List<String> postedBodies = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public boolean postDefect(Command cmd) {
        if (shouldFail) return false;
        
        // Simulate the format string generation logic that would happen in the real adapter
        // For the RED phase, we generate a placeholder. 
        // If we want the test to pass immediately (Green), we'd need to inject the URL, 
        // but in TDD Red, we want to ensure the VERIFICATION logic works.
        
        // Note: In a real flow, the Command passed here likely needs to carry the URL info
        // or the adapter needs to fetch it. For this mock, we simulate storing the event.
        
        String simulatedBody = "Defect reported: " + cmd.getClass().getSimpleName();
        postedBodies.add(simulatedBody);
        return true;
    }

    /**
     * Helper method for tests to inspect the state.
     */
    public String getLastPostedBody() {
        if (postedBodies.isEmpty()) return null;
        return postedBodies.get(postedBodies.size() - 1);
    }

    /**
     * Allows tests to inject a specific body to simulate the real implementation's output
     * or to verify that the Step logic correctly identifies the content.
     */
    public void recordSimulatedBody(String body) {
        postedBodies.add(body);
    }

    public void setShouldFail(boolean flag) {
        this.shouldFail = flag;
    }
}
