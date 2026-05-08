package com.example.steps;

import com.example.ports.SlackNotificationPort;
import com.example.mocks.MockSlackNotificationAdapter;
import com.example.domain.validation.ValidationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Story S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 * This represents the RED phase of TDD.
 * 
 * Context: The temporal-worker (via VForce360) reports a defect. We need to ensure
 * that when the defect is processed, the resulting Slack message contains the
 * GitHub issue URL.
 */
public class SFB1Steps {

    /**
     * Scenario: Verify Slack body contains GitHub issue link.
     * 
     * Given a defect report exists with ID VW-454
     * And the defect report contains a valid GitHub URL
     * When the validation service processes the report_defect workflow
     * Then the resulting Slack notification body should include the GitHub URL
     */
    @Test
    public void testSlackBodyContainsGithubUrl() {
        // Arrange
        // We mock the Slack port to capture output instead of sending real HTTP requests
        SlackNotificationPort mockSlack = new MockSlackNotificationAdapter();
        ValidationService service = new ValidationService(mockSlack);

        String defectId = "VW-454";
        String expectedUrl = "https://github.com/example/bank-of-z/issues/454";

        // Act
        // Trigger the defect reporting logic via the service
        service.reportDefect(defectId, expectedUrl);

        // Assert
        // Verify the Slack adapter received the payload
        assertTrue(mockSlack.wasCalled(), "Slack notification should have been triggered");
        
        String sentPayload = mockSlack.getLastPayload();
        assertNotNull(sentPayload, "Payload should not be null");

        // Critical Check: The body must contain the URL
        // This mimics the user checking the #vforce360-issues channel
        assertTrue(
            sentPayload.contains(expectedUrl), 
            "Slack body must include the GitHub issue URL: " + expectedUrl
        );
    }

    /**
     * Scenario: Verify Slack body is not empty when URL is missing (Edge Case).
     * Ensures robustness of the validation logic.
     */
    @Test
    public void testSlackBodyHandlesMissingUrl() {
        // Arrange
        SlackNotificationPort mockSlack = new MockSlackNotificationAdapter();
        ValidationService service = new ValidationService(mockSlack);

        String defectId = "VW-455";
        String missingUrl = null;

        // Act
        service.reportDefect(defectId, missingUrl);

        // Assert
        assertTrue(mockSlack.wasCalled());
        String payload = mockSlack.getLastPayload();
        
        // We expect a valid JSON structure even if the URL is null
        assertNotNull(payload);
        assertFalse(payload.isEmpty());
    }
}
