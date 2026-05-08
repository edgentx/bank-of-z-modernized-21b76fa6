package com.example.domain.defect;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression test for Story S-FB-1 (VW-454).
 * 
 * Tests the behavior of the Temporal worker activity logic responsible for
 * reporting defects. The primary validation is ensuring that when a defect
 * is reported with a GitHub URL, the Slack message body actually contains
 * that URL.
 * 
 * Corresponds to:
 * Story: S-FB-1
 * Defect: VW-454
 * Component: validation
 */
@DisplayName("S-FB-1: VW-454 Regression Tests")
class ReportDefectValidationTest {

    private MockSlackNotificationPort slackPort;

    @BeforeEach
    void setUp() {
        slackPort = new MockSlackNotificationPort();
    }

    @Test
    @DisplayName("Given a valid defect report, When report_defect is triggered, Then Slack body must contain GitHub URL")
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String expectedUrl = "https://github.com/example/repo/issues/454";
        String defectId = "VW-454";
        
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Validation Logic Failure",
            "The validation logic is failing to inject the GitHub URL into the Slack payload.",
            expectedUrl,
            "#vforce360-issues"
        );

        // Act
        // This simulates the Temporal activity execution.
        // Implementation of this class is expected to be added in the Green phase.
        ReportDefectHandler handler = new ReportDefectHandler(slackPort);
        handler.execute(cmd);

        // Assert
        MockSlackNotificationPort.PostedMessage result = slackPort.getSingleMessage();
        
        assertNotNull(result, "A message should have been posted to Slack");
        assertEquals("#vforce360-issues", result.channel, "The message should be routed to the correct channel");
        
        // CRITICAL ASSERTION: Verify the fix for VW-454
        assertTrue(
            result.body.contains(expectedUrl), 
            "Slack body must include the GitHub issue URL. Actual body: [" + result.body + "]"
        );
    }

    @Test
    @DisplayName("Given a defect with a null URL, When report_defect is triggered, Then throw validation exception")
    void testValidationFailsForNullUrl() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-455",
            "Missing URL",
            "No URL provided",
            null, // Violates validation rule
            "#vforce360-issues"
        );

        // Act
        ReportDefectHandler handler = new ReportDefectHandler(slackPort);
        
        // Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            handler.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("GitHub Issue URL is required"));
    }
}
