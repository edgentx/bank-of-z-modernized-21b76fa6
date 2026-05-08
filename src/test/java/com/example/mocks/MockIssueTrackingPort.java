package com.example.mocks;

import com.example.ports.IssueTrackingPort;
import java.util.Optional;

/**
 * In-memory mock implementation of {@link com.example.ports.IssueTrackingPort} for testing.
 */
public class MockIssueTrackingPort implements IssueTrackingPort {

    private String nextIssueUrl;
    private boolean shouldReturnEmpty = false;

    @Override
    public Optional<String> createRemoteIssue(String defectDetails) {
        if (shouldReturnEmpty) {
            return Optional.empty();
        }
        // Default mock behavior if not configured: return a dummy URL
        // In a real test setup, we'd configure this specific return value.
        return Optional.ofNullable(this.nextIssueUrl);
    }

    /**
     * Configures the mock to return a specific URL on the next call.
     */
    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    /**
     * Configures the mock to return an empty Optional, simulating a failed creation.
     */
    public void setReturnEmpty() {
        this.shouldReturnEmpty = true;
    }

    public void reset() {
        this.nextIssueUrl = null;
        this.shouldReturnEmpty = false;
    }
}
