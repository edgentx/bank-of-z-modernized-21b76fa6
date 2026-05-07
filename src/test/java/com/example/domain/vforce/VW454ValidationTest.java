package com.example.domain.vforce;

import com.example.ports.SlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase for Story S-FB-1.
 * 
 * Defect: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * Severity: LOW
 * 
 * Reproduction Steps:
 * 1. Trigger _report_defect via temporal-worker exec
 * 2. Verify Slack body contains GitHub issue link
 * 
 * Expected Behavior: Slack body includes GitHub issue: <url>
 * Actual Behavior: To be determined (suspected missing link).
 */
public class VW454ValidationTest {

    // Mocks for external dependencies (Adapters)
    private GitHubIssuePort mockGitHub;
    private SlackNotificationPort mockSlack;

    @BeforeEach
    void setUp() {
        // Initialize mocks. In a real Spring setup, we might use @MockBean, 
        // but for pure unit testing the domain logic, manual mocking or Mockito 
        // is clearer.
        mockGitHub = mock(GitHubIssuePort.class);
        mockSlack = mock(SlackNotificationPort.class);
    }

    @Test
    void testDefectReportFlow_includesGitHubLinkInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String defectSummary = "Validating VW-454";
        String expectedGitHubUrl = "https://github.com/bank-of-z/vforce360/issues/454";

        // Configure the mock GitHub port to return a specific URL
        when(mockGitHub.createIssue(anyString(), anyString()))
            .thenReturn(expectedGitHubUrl);

        // Act
        // We are testing the 'ReportDefectCommand' handler logic.
        // Since the implementation doesn't exist yet, we simulate the call 
        // that the Temporal worker would make.
        ReportDefectHandler handler = new ReportDefectHandler(mockGitHub, mockSlack);
        handler.execute(new ReportDefectCommand(defectId, defectSummary));

        // Assert
        // 1. Verify GitHub port was called (interaction test)
        verify(mockGitHub).createIssue(eq(defectId), contains(defectSummary));

        // 2. Verify Slack port was called (interaction test)
        verify(mockSlack).notify(anyString());

        // 3. CRITICAL ASSERTION: Verify the Slack body actually contains the URL.
        // This is the core acceptance criteria for the defect fix.
        // We capture the argument passed to the mock to inspect it.
        ArgumentCaptor<String> slackBodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSlack).notify(slackBodyCaptor.capture());
        
        String actualSlackBody = slackBodyCaptor.getValue();
        assertTrue(
            actualSlackBody.contains(expectedGitHubUrl),
            "Regression Test Failed: Slack body must contain the GitHub issue URL.\nExpected URL: " + expectedGitHubUrl + "\nActual Body: " + actualSlackBody
        );
    }

    @Test
    void testDefectReportFlow_handlesGitHubFailure() {
        // Arrange
        when(mockGitHub.createIssue(anyString(), anyString()))
            .thenThrow(new RuntimeException("GitHub API Timeout"));

        // Act
        ReportDefectHandler handler = new ReportDefectHandler(mockGitHub, mockSlack);
        
        // Assert
        // System should either throw or handle gracefully. 
        // Assuming we propagate failure for now in TDD.
        assertThrows(RuntimeException.class, () -> {
            handler.execute(new ReportDefectCommand("VW-999", "Simulated Failure"));
        });
    }
}
