package com.example.mocks;

import com.example.domain.shared.SlackMessageValidator;

/**
 * Mock implementation of SlackMessageValidator for testing purposes.
 * Can be configured to return true/false based on test needs.
 */
public class MockSlackMessageValidator implements SlackMessageValidator {

    private boolean shouldPass;

    public MockSlackMessageValidator(boolean shouldPass) {
        this.shouldPass = shouldPass;
    }

    @Override
    public boolean isValid(String messageBody) {
        // Basic mock behavior: return configured boolean.
        // A real implementation might check for substrings, but for tests, we control the outcome.
        return this.shouldPass;
    }

    public void setShouldPass(boolean shouldPass) {
        this.shouldPass = shouldPass;
    }
}
