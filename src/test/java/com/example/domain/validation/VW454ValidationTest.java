package com.example.domain.validation;

import com.example.ports.SlackNotifier;
import com.example.ports.GitHubClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TDD Red Phase Test for VW-454.
 * 
 * Scenario: Verifying that a defect report triggered via Temporal 
 * results in a Slack notification containing the GitHub issue URL.
 * 
 * Acceptance Criteria:
 * 1. The validation logic executes without error.
 * 2. The resulting Slack body contains the valid GitHub URL.
 */
public class VW454ValidationTest {

    @Test
    public void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/example/repo/issues/454";
        
        // Mock external dependencies (Ports)
        SlackNotifier mockSlack = mock(SlackNotifier.class);
        GitHubClient mockGitHub = mock(GitHubClient.class);
        
        // Configure mock GitHub to return a valid URL when queried
        when(mockGitHub.createIssue(anyString(), anyString())).thenReturn(expectedUrl);

        // System Under Test (SUT)
        // We use the concrete implementation class that would exist in src/main/java.
        // Since this is TDD Red Phase, this class might not exist yet, or the logic is missing.
        // We define the interface 'DefectReporter' locally for the test structure.
        DefectReporter reporter = new DefectReporter(mockGitHub, mockSlack);

        // Act
        reporter.reportDefect(defectId, "Validation fix required");

        // Assert
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSlack).sendNotification(messageCaptor.capture());
        
        String actualSlackBody = messageCaptor.getValue();
        
        // The core validation: The Slack body MUST contain the GitHub URL.
        // If this test fails, the URL is missing or malformed.
        assertTrue(
            actualSlackBody.contains(expectedUrl), 
            "Expected Slack body to contain GitHub issue URL: " + expectedUrl + ", but got: " + actualSlackBody
        );
        
        // Verify integration with GitHub service was attempted
        verify(mockGitHub).createIssue(eq(defectId), anyString());
    }
}
