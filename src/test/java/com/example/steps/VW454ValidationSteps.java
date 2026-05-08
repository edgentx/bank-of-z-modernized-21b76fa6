package com.example.steps;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.service.DefectReportService;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454.
 * Verifies that when a defect is reported, the Slack notification body
 * includes the GitHub issue URL.
 */
public class VW454ValidationSteps {

    private MockSlackNotificationPort slackPort;
    private DefectReportService defectReportService;

    @BeforeEach
    public void setUp() {
        // Initialize Mock Adapter
        slackPort = new MockSlackNotificationPort();
        // Initialize Service with mock dependencies
        defectReportService = new DefectReportService(slackPort);
    }

    @Test
    public void testReportDefect_ShouldContainGitHubUrl() {
        // Given
        String defectId = "VW-454";
        String title = "GitHub URL in Slack body (end-to-end)";
        String expectedUrl = "https://github.com/example/issues/" + defectId;

        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            title,
            "Slack body includes GitHub issue link",
            "LOW",
            "validation"
        );

        // When
        defectReportService.report(cmd);

        // Then
        String actualBody = slackPort.getLastMessageBody();
        assertNotNull(actualBody, "Slack body should not be null");
        
        // Strict assertion for the URL presence
        assertTrue(
            actualBody.contains(expectedUrl),
            "Slack body must contain the GitHub issue URL: " + expectedUrl + "\nActual: " + actualBody
        );
    }

    @Test
    public void testReportDefect_SlackBodyShouldContainPrefix() {
        // Given
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "Test Defect",
            "Checking URL format",
            "LOW",
            "validation"
        );

        // When
        defectReportService.report(cmd);

        // Then
        String actualBody = slackPort.getLastMessageBody();
        assertTrue(actualBody.contains("GitHub issue:"), "Body should indicate the context of the URL");
    }
}
