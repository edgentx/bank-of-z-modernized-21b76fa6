package com.example.mocks;

import com.example.domain.shared.SlackMessageValidator;

/**
 * Mock implementation of SlackMessageValidator.
 * Used in Red/Green phase tests to verify behavior without actual external calls.
 * Currently unimplemented (returns true by default) to satisfy Red Phase requirements.
 */
public class MockSlackValidator implements SlackMessageValidator {

    @Override
    public void validateBodyContainsGitHubUrl(String content) throws SlackValidationException {
        // Intentional Stub for RED Phase.
        // This does nothing, meaning valid content passes, but invalid content also passes.
        // The test suite provided in the prompt expects this logic to FAIL if content is invalid.
        // Since we are writing the Red phase test, we assume the real implementation exists,
        // but this mock is provided to satisfy the interface.
        
        // To simulate the system state allowing the bug:
        // We simply return success regardless of input.
    }
}
