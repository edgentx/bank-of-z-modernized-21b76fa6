package com.example.domain.vforce;

import com.example.ports.VForce360Port;
import com.example.mocks.MockSlackNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;

/**
 * TDD Red Phase for Story S-FB-1.
 * Validating VW-454 — GitHub URL in Slack body (end-to-end).
 *
 * Tests that when a defect is reported, the resulting Slack notification
 * contains a valid link to the GitHub issue.
 */
class SlackNotificationValidatorTest {

    private MockSlackNotificationService mockSlack;
    private VForce360Port vForceService;

    @BeforeEach
    void setUp() {
        // We use a real implementation of the service logic (VForce360Port/Service)
        // but inject a Mock for the external Slack API call.
        mockSlack = new MockSlackNotificationService();
        vForceService = new VForce360Service(mockSlack);
    }

    @Test
    void testReportDefect_generatesValidGitHubUrlInSlackBody() {
        // Given
        String defectTitle = "VW-454: GitHub URL validation";
        String defectBody = "Reproduction steps: ...";
        String expectedRepo = "https://github.com/example/bank-of-z";
        
        // When
        // We simulate the execution of _report_defect via temporal-worker
        String result = vForceService.reportDefect(defectTitle, defectBody, expectedRepo);

        // Then
        assertNotNull(result, "Result should indicate success or return an ID");
        
        // Verify the internal state (mock) captured the Slack payload
        assertTrue(mockSlack.wasCalled(), "Slack notification should have been triggered");
        
        String postedBody = mockSlack.getLastMessageBody();
        assertNotNull(postedBody, "Slack body should not be null");
        
        // Critical validation: The body must contain the GitHub URL
        // This currently FAILS (Red phase) because VForce360Service is likely empty
        assertTrue(
            postedBody.contains("http"), 
            "Slack body should contain a GitHub issue URL (http)"
        );

        // Verify the specific pattern matches the repo
        assertTrue(
            postedBody.contains(expectedRepo),
            "Slack body should contain the specific repository URL: " + expectedRepo
        );
    }

    @Test
    void testReportDefect_handlesEmptyRepoNameGracefully() {
        // Edge case: Empty repo
        String result = vForceService.reportDefect("Test", "Desc", "");
        
        // Should not throw, should still attempt to notify
        assertTrue(mockSlack.wasCalled());
        // Ideally body shouldn't have broken links, but strict enforcement depends on requirements.
        // For S-FB-1, we primarily care that valid inputs produce valid outputs.
    }
}
