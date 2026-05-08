package com.example.adapters;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.domain.validation.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link SlackValidationAdapter}.
 * Verifies that defects reported via Temporal are correctly formatted and sent to Slack.
 */
class SlackValidationAdapterTest {

    /**
     * Validates VW-454: Regression test for GitHub URL in Slack body.
     * 
     * Given a defect report command containing valid metadata (Project ID, Severity, Title)
     * When the reportDefect method is executed
     * Then the resulting Slack message payload must contain a correctly formatted GitHub issue URL.
     */
    @Test
    void testReportDefect_ContainsGitHubUrl() {
        // Arrange
        var mockSlackPort = mock(SlackPort.class);
        var mockGitHubPort = mock(GitHubPort.class);
        
        String expectedProjectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        String expectedTitle = "VW-454: GitHub URL in Slack body";
        String expectedSeverity = "LOW";
        String expectedUrl = "https://github.com/egdcrypto/bank-of-z-modernized/issues/454";

        // Configure GitHub mock to return a specific URL
        when(mockGitHubPort.createIssue(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(expectedUrl);

        var adapter = new SlackValidationAdapter(mockSlackPort, mockGitHubPort);
        var cmd = new ReportDefectCmd(
            expectedProjectId, 
            expectedSeverity, 
            "validation", 
            expectedTitle, 
            "Description of the defect"
        );

        // Act
        adapter.reportDefect(cmd);

        // Assert
        // Verify that the port was called
        verify(mockSlackPort).sendMessage(argThat(payload -> {
            String body = payload.get("text");
            // The test passes only if the body contains the exact URL returned by the mock
            return body != null && body.contains(expectedUrl);
        }));
        
        // Verify GitHub was called with valid parameters (Side effect check)
        verify(mockGitHubPort).createIssue(
            eq(expectedTitle),
            anyString(), // body
            eq(expectedProjectId), // project label
            eq(expectedSeverity)  // severity label
        );
    }

    @Test
    void testReportDefect_SlackBodyFormat() {
        // Arrange
        var mockSlackPort = mock(SlackPort.class);
        var mockGitHubPort = mock(GitHubPort.class);
        
        when(mockGitHubPort.createIssue(anyString(), anyString(), anyString(), anyString()))
            .thenReturn("https://github.com/test/issues/1");

        var adapter = new SlackValidationAdapter(mockSlackPort, mockGitHubPort);
        var cmd = new ReportDefectCmd(
            "p-id-1", 
            "HIGH", 
            "auth", 
            "Security breach", 
            "System wide failure"
        );

        // Act
        adapter.reportDefect(cmd);

        // Assert
        verify(mockSlackPort).sendMessage(argThat(payload -> {
            String text = payload.get("text");
            return text.contains("New Defect Reported") 
                && text.contains("Project: p-id-1")
                && text.contains("Severity: HIGH")
                && text.contains("https://github.com/test/issues/1");
        }));
    }
}
