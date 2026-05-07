package com.example.service;

import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.MockGitHubAdapter;
import com.example.mocks.MockSlackAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for VW-454.
 * 
 * Validating that when a defect is reported, the resulting Slack notification
 * includes the URL of the created GitHub issue.
 */
class DefectReportServiceTest {

    private GitHubPort mockGitHub;
    private SlackPort mockSlack;
    private DefectReportService service;

    @BeforeEach
    void setUp() {
        mockGitHub = new MockGitHubAdapter();
        mockSlack = new MockSlackAdapter();
        service = new DefectReportService(mockGitHub, mockSlack);
    }

    @Test
    void testReportDefect_CreatesGitHubIssue_ThenSendsSlackNotificationWithUrl() {
        // 1. Setup: Configure the Mock GitHub to return a specific URL
        String expectedGitHubUrl = "https://github.com/mock-org/bank-of-z/issues/454";
        ((MockGitHubAdapter) mockGitHub).setMockUrl(expectedGitHubUrl);

        // 2. Execute: Trigger the defect report command
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454", 
            "GitHub URL in Slack body missing", 
            "LOW", 
            "validation", 
            Instant.now()
        );

        service.execute(cmd);

        // 3. Verify (RED Phase Failure Conditions):
        // We expect the Slack adapter to have received a message containing the GitHub URL.
        MockSlackAdapter slackSpy = (MockSlackAdapter) mockSlack;
        MockSlackAdapter.Message sentMessage = slackSpy.getLastMessage();

        assertNotNull(sentMessage, "Slack should have received a message");
        
        // This assertion enforces the Expected Behavior: "Slack body includes GitHub issue: <url>"
        assertTrue(
            sentMessage.text.contains(expectedGitHubUrl), 
            "Slack body must include the GitHub issue URL. Found: " + sentMessage.text
        );
        
        // Ensure it was sent to the correct channel
        assertEquals("#vforce360-issues", sentMessage.channel);
    }

    @Test
    void testReportDefect_GitHubReturnsNull_ThrowsException() {
        // Edge case: GitHub service fails to return a URL
        ((MockGitHubAdapter) mockGitHub).setMockUrl(null);

        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-999", "Broken", "HIGH", "validation", Instant.now()
        );

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            service.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("GitHub URL"));
    }
}
