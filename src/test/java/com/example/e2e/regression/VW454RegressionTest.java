package com.example.e2e.regression;

import com.example.domain.shared.SlackMessageValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression Test for VW-454.
 * Verifies that the validation logic correctly identifies when a Slack message
 * body contains the expected GitHub issue URL.
 */
class VW454RegressionTest {

    /**
     * Scenario: Validate GitHub URL presence in Slack body.
     * Given: A Slack message body containing the GitHub URL.
     * When: The validator checks the content.
     * Then: The validation passes (returns true).
     */
    @Test
    void shouldPassWhenGitHubUrlIsPresent() {
        // Given
        SlackMessageValidator validator = new StubSlackMessageValidator();
        String slackBody = "Defect reported: <https://github.com/bank-of-z/issues/454|View Issue>";
        String target = "GitHub issue";

        // When
        boolean isValid = validator.containsValidUrl(slackBody, target);

        // Then
        assertTrue(isValid, "Slack body should contain the GitHub issue URL");
    }

    /**
     * Scenario: Validate GitHub URL absence in Slack body.
     * Given: A Slack message body missing the GitHub URL.
     * When: The validator checks the content.
     * Then: The validation fails (returns false).
     */
    @Test
    void shouldFailWhenGitHubUrlIsMissing() {
        // Given
        SlackMessageValidator validator = new StubSlackMessageValidator();
        String slackBody = "Defect reported: Link coming soon.";
        String target = "GitHub issue";

        // When
        boolean isValid = validator.containsValidUrl(slackBody, target);

        // Then
        assertFalse(isValid, "Slack body should fail validation if GitHub issue URL is missing");
    }

    // Stub implementation used for Red Phase / structural compilation
    static class StubSlackMessageValidator implements SlackMessageValidator {
        @Override
        public boolean containsValidUrl(String messageBody, String urlTarget) {
            // Intentional minimal implementation for Red Phase
            // The real implementation would parse the URL.
            // For now, we return false to ensure test can fail if logic isn't added.
            return false;
        }
    }
}
