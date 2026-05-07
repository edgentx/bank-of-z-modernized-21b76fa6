package com.example.mocks;

import com.example.ports.IssueTrackerPort;
import java.util.Optional;

/**
 * Mock implementation of IssueTrackerPort for testing.
 * Allows configuration of specific URLs for issue IDs.
 */
public class MockIssueTrackerPort implements IssueTrackerPort {

    private String mockUrlPrefix = "https://github.com/mock-repo/issues/";
    private boolean alwaysReturnEmpty = false;

    @Override
    public Optional<IssueUrl> getIssueUrl(String issueId) {
        if (alwaysReturnEmpty) {
            return Optional.empty();
        }
        // Basic simulation logic
        if (issueId != null && !issueId.isBlank()) {
            return Optional.of(new IssueUrl(mockUrlPrefix + issueId));
        }
        return Optional.empty();
    }

    public void setMockUrlPrefix(String prefix) {
        this.mockUrlPrefix = prefix;
    }

    public void setAlwaysReturnEmpty(boolean isEmpty) {
        this.alwaysReturnEmpty = isEmpty;
    }
}
