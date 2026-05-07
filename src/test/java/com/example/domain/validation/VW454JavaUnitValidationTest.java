package com.example.domain.validation;

import com.example.application.DefectReportingService;
import com.example.domain.shared.ReportDefectCommand;
import com.example.mocks.MockNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure JUnit 5 Test for VW-454 logic validation.
 * This confirms the defect is fixed at a unit level.
 */
class VW454JavaUnitValidationTest {

    private MockNotificationPort mockPort;
    private DefectReportingService service;

    @BeforeEach
    void setUp() {
        mockPort = new MockNotificationPort();
        service = new DefectReportingService(mockPort);
    }

    @Test
    void testDefectReport_generatesSlackMessageWithGitHubLink() {
        // Arrange
        String expectedGithubUrl = "http://github.com/org/project/issues/123";
        mockPort.githubUrlToReturn = expectedGithubUrl;

        ReportDefectCommand cmd = new ReportDefectCommand(
            "DEFECT-1",
            "Test Title",
            "Test Description",
            "HIGH",
            "COMP-01"
        );

        // Act
        service.handle(cmd);

        // Assert
        assertEquals(1, mockPort.slackCalls.size(), "Expected one Slack call");
        
        String slackBody = mockPort.slackCalls.get(0).message();
        
        // AC: The validation no longer exhibits the reported behavior
        // The reported behavior (implicitly defined as missing URL) is fixed if this passes.
        assertTrue(
            slackBody.contains(expectedGithubUrl),
            "Slack body must include the GitHub URL.\nExpected: " + expectedGithubUrl + "\nActual: " + slackBody
        );
        
        // Verify the GitHub URL format is correct (<url>)
        assertTrue(
            slackBody.contains("<" + expectedGithubUrl + ">"),
            "Slack body should format the URL as a link <url>."
        );
    }

    @Test
    void testSlackBodyFormat_ContainsRequiredFields() {
        // Arrange & Act
        mockPort.githubUrlToReturn = "http://github.com/org/project/issues/454";
        ReportDefectCommand cmd = new ReportDefectCommand(
            "S-FB-1",
            "VW-454 Issue",
            "Slack body missing link",
            "LOW",
            "validation"
        );
        
        service.handle(cmd);

        // Assert
        String slackBody = mockPort.slackCalls.get(0).message();
        
        assertTrue(slackBody.contains("Severity: LOW"), "Must contain Severity");
        assertTrue(slackBody.contains("VW-454 Issue"), "Must contain Title");
        assertTrue(slackBody.contains("http://github.com/org/project/issues/454"), "Must contain GitHub Link");
    }
}
