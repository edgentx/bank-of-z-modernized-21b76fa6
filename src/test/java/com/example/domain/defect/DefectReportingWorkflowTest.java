package com.example.domain.defect;

import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for S-FB-1: Validating VW-454 — GitHub URL in Slack body.
 * 
 * Story: Triggering _report_defect via temporal-worker should result in a Slack notification
 * containing the GitHub issue link in the body.
 * 
 * These tests are designed to FAIL initially against an empty implementation,
 * enforcing the required behavior for the regression fix.
 */
class DefectReportingWorkflowTest {

    private MockSlackNotificationPort slackPort;

    @BeforeEach
    void setUp() {
        slackPort = new MockSlackNotificationPort();
    }

    @Test
    void shouldContainGitHubUrlInSlackBody_WhenReportingDefect() {
        // Arrange
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/example/issues/454";
        
        // This class represents the domain logic handling the defect report.
        // In a real Temporal Workflow, this would be the Activity implementation.
        DefectReportingService service = new DefectReportingService(slackPort);

        // Act
        service.reportDefect(defectId, expectedUrl);

        // Assert
        // 1. Verify a notification was sent
        assertEquals(1, slackPort.getSentMessages().size(), "Slack notification should have been sent once");

        // 2. Verify the body contains the GitHub issue URL
        String actualBody = slackPort.getSentMessages().get(0);
        assertTrue(
            actualBody.contains(expectedUrl),
            "Slack body must include the GitHub issue URL. Expected: [" + expectedUrl + "] in: [" + actualBody + "]"
        );

        // 3. Verify the body indicates it is a GitHub link
        assertTrue(
            actualBody.contains("GitHub") || actualBody.contains("Issue"),
            "Slack body should reference the GitHub issue context."
        );
    }

    @Test
    void shouldFailValidation_WhenGitHubUrlIsMissing() {
        // Arrange
        String defectId = "VW-454";
        String missingUrl = null; // Simulating the defect state where URL is not generated
        
        DefectReportingService service = new DefectReportingService(slackPort);

        // Act & Assert
        // The service should throw an exception or handle the missing URL gracefully,
        // rather than sending a malformed Slack message.
        assertThrows(
            IllegalStateException.class,
            () -> service.reportDefect(defectId, missingUrl),
            "Service should throw exception if GitHub URL is not provided for the defect report."
        );
        
        // Verify no partial message was sent
        assertTrue(
            slackPort.getSentMessages().isEmpty(),
            "No Slack notification should be sent if the GitHub URL is missing."
        );
    }
}
