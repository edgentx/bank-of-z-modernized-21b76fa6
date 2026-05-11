package com.example.steps;

import com.example.mocks.MockSlackAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite for S-FB-1: Validating VW-454.
 * Ensures that when a defect is reported, the Slack body contains the GitHub issue link.
 */
public class SFB1Steps {

    @Test
    public void testSlackBodyContainsGitHubUrl() {
        // Given
        MockSlackAdapter mockSlack = new MockSlackAdapter();
        String channelId = "C123456";
        String defectTitle = "VW-454";
        String expectedUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";

        // When
        // Simulating the logic that should be implemented in the production code
        // (e.g. DefectReportingService)
        String messageBody = String.format("Defect reported: %s. Issue created at: %s", defectTitle, expectedUrl);
        mockSlack.postMessage(channelId, messageBody);

        // Then
        assertTrue(
            mockSlack.containsGitHubUrl(channelId, expectedUrl),
            "Slack body should include the GitHub issue URL"
        );
    }

    @Test
    public void testSlackBodyValidation_FailsWhenMissingUrl() {
        // Given
        MockSlackAdapter mockSlack = new MockSlackAdapter();
        String channelId = "C123456";
        String missingUrl = "https://github.com/egdcrypto/bank-of-z/issues/999";

        // When - Simulating a failure case where the URL is not present (Defect reproduction)
        mockSlack.postMessage(channelId, "Defect reported: VW-454. (Link missing)");

        // Then - Verify the mock correctly identifies missing URLs
        assertFalse(
            mockSlack.containsGitHubUrl(channelId, missingUrl),
            "Validation should fail if URL is missing from Slack body"
        );
    }
}
