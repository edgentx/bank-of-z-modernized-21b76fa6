package com.example.e2e.regression;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.mocks.MockDefectRepository;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import com.example.services.DefectReportingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454.
 * Validates that when a defect is reported, the Slack notification body
 * contains the correct GitHub issue URL.
 */
public class VW454_EndToEndTest {

    private DefectRepository defectRepository;
    private MockGitHubPort mockGitHubPort;
    private MockSlackNotificationPort mockSlackPort;
    private DefectReportingService service;

    @BeforeEach
    void setUp() {
        defectRepository = new MockDefectRepository();
        mockGitHubPort = new MockGitHubPort();
        mockSlackPort = new MockSlackNotificationPort();
        service = new DefectReportingService(defectRepository, mockGitHubPort, mockSlackPort);
    }

    @Test
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String defectId = "VW-454";
        String expectedGithubUrl = "https://github.com/egdcrypto/bank-of-z/issues/" + defectId;
        
        // Configure the mock GitHub service to return the specific URL we expect
        mockGitHubPort.setSimulatedUrl(expectedGithubUrl);

        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Fix: Validating VW-454",
            "GitHub URL missing in Slack body",
            "LOW",
            "validation"
        );

        // Act
        service.reportDefect(cmd);

        // Assert
        // 1. Verify the message was sent to the correct channel
        assertEquals("#vforce360-issues", mockSlackPort.lastChannel, "Should post to vforce360-issues channel");

        // 2. Verify the message content contains the GitHub URL (Primary Validation)
        assertTrue(
            mockSlackPort.wasUrlSent(expectedGithubUrl), 
            "Slack body must contain the GitHub issue URL"
        );

        // 3. Verify exact format (optional but good for regression)
        String sentMessage = mockSlackPort.sentMessages.get(0);
        assertTrue(sentMessage.contains("GitHub Issue: " + expectedGithubUrl), "URL should be prefixed with 'GitHub Issue:'");
    }
}
