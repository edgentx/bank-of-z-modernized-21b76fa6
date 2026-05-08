package com.example.application;

import com.example.mocks.MockGithubPort;
import com.example.mocks.MockSlackPort;
import com.example.vforce.shared.ReportDefectCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Regression test for VW-454.
 * Validates that the Slack notification body contains the GitHub issue URL.
 */
class ReportDefectWorkflowServiceTest {

    private MockGithubPort mockGithub;
    private MockSlackPort mockSlack;
    private ReportDefectWorkflowService service;

    @BeforeEach
    void setUp() {
        mockGithub = new MockGithubPort("https://github.com/fake/issues/454");
        mockSlack = new MockSlackPort();
        service = new ReportDefectWorkflowService(mockGithub, mockSlack);
    }

    @Test
    void shouldIncludeGitHubUrlInSlackBody() {
        // Given
        ReportDefectCommand cmd = new ReportDefectCommand(
                "Defect: Login fails",
                "User cannot login with valid credentials.",
                List.of("bug", "critical")
        );

        // When
        service.execute(cmd);

        // Then
        // 1. Verify Github Port was called
        assertEquals(1, mockGithub.receivedCommands.size());
        assertEquals("Defect: Login fails", mockGithub.receivedCommands.get(0).title());

        // 2. Verify Slack Port was called
        String slackMessage = mockSlack.getLastMessage();
        assertNotNull(slackMessage, "Slack message should not be null");

        // 3. CRITICAL ASSERTION: The URL must be present in the body (VW-454)
        assertTrue(slackMessage.contains("https://github.com/fake/issues/454"),
                "Slack body MUST contain the GitHub URL. VW-454 Regression detected.");
    }
}