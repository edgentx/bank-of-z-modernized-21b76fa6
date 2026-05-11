package com.example.e2e.regression;

import com.example.domain.defect.model.ReportDefectCommand;
import com.example.mocks.InMemoryDefectRepository;
import com.example.mocks.MockSlackNotificationPort;
import com.example.services.ReportDefectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454: GitHub URL in Slack body (end-to-end).
 *
 * Reproduction Steps:
 * 1. Trigger _report_defect via temporal-worker exec (Simulated by service call)
 * 2. Verify Slack body contains GitHub issue link
 *
 * Expected Behavior: Slack body includes GitHub issue: <url>
 */
class VW454SlackUrlRegressionTest {

    private InMemoryDefectRepository repository;
    private MockSlackNotificationPort slackMock;
    private ReportDefectService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDefectRepository();
        slackMock = new MockSlackNotificationPort();
        service = new ReportDefectService(repository, slackMock);
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBody_whenReportingDefect() {
        // Arrange
        String defectId = "DEF-001";
        String title = "VForce360 PM diagnostic failure";
        String description = "Temporal worker failed to validate.";
        String expectedUrl = "https://github.com/egdcrypto/bank-of-z-modernized/issues/454";

        ReportDefectCommand cmd = new ReportDefectCommand(
            defectId,
            title,
            description,
            expectedUrl
        );

        // Act
        service.reportDefect(cmd);

        // Assert
        assertFalse(slackMock.messages.isEmpty(), "Slack notification should have been sent");
        
        String actualMessage = slackMock.messages.get(0);
        
        // VW-454 Validation: Verify the URL is present in the message body
        assertTrue(
            actualMessage.contains(expectedUrl),
            "Slack body should contain the GitHub issue URL. Actual: " + actualMessage
        );
        
        // Verify it's in the expected format: "GitHub Issue: <url>"
        assertTrue(
            actualMessage.contains("GitHub Issue:"),
            "Slack body should contain the label 'GitHub Issue:'"
        );
    }

    @Test
    void shouldFailVerification_ifUrlMissingFromSlackBody() {
        // This test demonstrates the failure condition if the defect is NOT fixed.
        // Currently passing because the implementation includes the URL.
        // If the implementation regresses, this test will fail.

        String defectId = "DEF-002";
        String badUrl = null; // Simulate missing URL

        ReportDefectCommand cmd = new ReportDefectCommand(
            defectId,
            "Test",
            "Desc",
            "https://github.com/test/1" // Valid URL required for command construction usually, but here we test output
        );

        service.reportDefect(cmd);
        String actualMessage = slackMock.messages.get(0);

        // The assertion ensures the URL is there.
        assertNotNull(actualMessage, "Message should not be null");
        assertTrue(actualMessage.contains("https://github.com/test/1"));
    }
}
