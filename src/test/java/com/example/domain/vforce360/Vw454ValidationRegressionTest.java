package com.example.domain.vforce360;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for Defect VW-454.
 * 
 * Verifies that triggering _report_defect results in a Slack body containing
 * the GitHub issue link.
 * 
 * Corresponds to: S-FB-1
 */
class Vw454ValidationRegressionTest {

    private VForce360Aggregate aggregate;
    private MockSlackPort mockSlack;
    private MockGitHubPort mockGitHub;

    @BeforeEach
    void setUp() {
        // In a real app, these might be injected, but for unit testing we instantiate manually.
        mockSlack = new MockSlackPort();
        mockGitHub = new MockGitHubPort();
        
        // We expect the Aggregate to take dependencies (Ports) in its constructor.
        // This file will NOT compile until VForce360Aggregate is created.
        aggregate = new VForce360Aggregate("defect-1", mockGitHub, mockSlack);
    }

    @Test
    void testReportDefect_ShouldContainGitHubUrlInSlackBody() {
        // Arrange
        String expectedGitHubUrl = "https://github.com/bank-of-z/vforce360/issues/454";
        mockGitHub.setMockUrl(expectedGitHubUrl);
        
        ReportDefectCmd cmd = new ReportDefectCmd("vw-454", "VW-454: GitHub URL missing", "Slack body is missing the link.");

        // Act
        // Expecting a DomainEvent or void return. Assuming standard Aggregate execute pattern.
        assertDoesNotThrow(() -> aggregate.execute(cmd));

        // Assert
        assertEquals(1, mockSlack.messages.size(), "Slack should receive exactly one message");
        
        MockSlackPort.Message slackMsg = mockSlack.messages.get(0);
        assertEquals("#vforce360-issues", slackMsg.channel(), "Should post to the correct channel");
        
        // CRITICAL ASSERTION for VW-454: The body must contain the URL.
        assertTrue(
            slackMsg.body().contains(expectedGitHubUrl), 
            "Slack body must include the GitHub issue URL.\nActual body: " + slackMsg.body()
        );
    }

    @Test
    void testReportDefect_ShouldCallGitHub() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd("vw-454", "Title", "Desc");

        // Act
        assertDoesNotThrow(() -> aggregate.execute(cmd));

        // Assert
        assertTrue(mockGitHub.wasCreateIssueCalled(), "GitHub createIssue must be called during defect reporting");
    }

    @Test
    void testReportDefect_WithEmptyGitHubUrl_ShouldHandleGracefully() {
        // Edge case: what if GitHub returns empty?
        mockGitHub.setMockUrl("");
        ReportDefectCmd cmd = new ReportDefectCmd("vw-empty", "Empty URL", "Desc");

        assertDoesNotThrow(() -> aggregate.execute(cmd));
        // Depending on business logic, we might want to check if Slack still sends, 
        // or if it sends a 'failed' message. For now, just ensuring no crash is Red-phase minimal.
    }
}
