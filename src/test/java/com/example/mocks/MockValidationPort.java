package com.example.mocks;

import com.example.domain.shared.validation.ValidationPort;
import java.util.Map;

/**
 * Mock implementation of ValidationPort for testing workflows.
 * Configurable to pass or fail validation scenarios.
 */
public class MockValidationPort implements ValidationPort {

    private String simulatedUrl;
    private boolean shouldFail = false;

    public void setSimulatedUrl(String url) {
        this.simulatedUrl = url;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public String extractAndValidateGithubUrl(Map<String, Object> context) {
        if (shouldFail) {
            throw new IllegalArgumentException("Mock validation failure");
        }
        // In a happy path, we return the configured URL or a default if not set
        return simulatedUrl != null ? simulatedUrl : "https://github.com/example/issues/1";
    }
}