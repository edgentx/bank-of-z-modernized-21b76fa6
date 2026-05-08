package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for Defect Reporting (VW-454).
 * 
 * Acceptance Criteria:
 * 1. The validation no longer exhibits the reported behavior (Missing GitHub URL in Slack body).
 * 2. Regression test added to e2e/regression/ covering this scenario.
 * 
 * Context:
 * The defect report indicates that when a defect is triggered, the resulting Slack notification
 * must contain the actual link to the created GitHub issue.
 */
class DefectAggregateTest {

    private MockGitHubIssuePort mockGitHub;
    private MockSlackNotificationPort mockSlack;
    private DefectAggregate aggregate;

    @BeforeEach
    void setUp() {
        mockGitHub = new MockGitHubIssuePort();
        mockSlack = new MockSlackNotificationPort();
        // ID matches the story ID: S-FB-1 or the Defect ID VW-454
        aggregate = new DefectAggregate("VW-454", mockGitHub, mockSlack);
    }

    @Test
    void testExecute_ReportDefect_Success() {
        // Arrange
        String expectedTitle = "Fix: Validating VW-454";
        String expectedDesc = "Repro steps...";
        mockGitHub.setMockUrl("https://github.com/bank-of-z/core/issues/454");

        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            expectedTitle,
            expectedDesc,
            "LOW",
            "validation"
        );

        // Act
        List<DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty(), "Should produce an event");
        assertTrue(events.get(0) instanceof DefectReportedEvent);

        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals("https://github.com/bank-of-z/core/issues/454", event.githubUrl());
        assertEquals("#vforce360-issues", event.slackChannel());
    }

    /**
     * Primary Regression Test for VW-454.
     * Verifies that the Slack body actually contains the GitHub URL.
     */
    @Test
    void testExecute_SlackBodyContainsGitHubURL_RegressionTest() {
        // Arrange
        String specificGitHubUrl = "https://github.com/project/repo/issues/999";
        mockGitHub.setMockUrl(specificGitHubUrl);

        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "Defect Title",
            "Description",
            "LOW",
            "validation"
        );

        // Act
        aggregate.execute(cmd);

        // Assert - Verify Mock Slack Port received the body
        // We expect the body to contain the URL.
        MockSlackNotificationPort.SlackMessage msg = mockSlack.getMessages().get(0);
        
        assertNotNull(msg, "Slack should have received a message");
        assertEquals("#vforce360-issues", msg.channel, "Channel should be #vforce360-issues");
        
        // Critical assertion: The body must include the GitHub URL link
        // This checks the fix for "Slack body includes GitHub issue: <url>"
        assertTrue(
            msg.body.contains(specificGitHubUrl), 
            "Slack body must contain the GitHub URL. VW-454 regression check.\nActual Body: " + msg.body
        );
    }

    @Test
    void testExecute_GitHubFailure_ThrowsException() {
        // Arrange
        mockGitHub.setShouldFail(true); // GitHub returns null/empty
        ReportDefectCmd cmd = new ReportDefectCmd("VW-454", "T", "D", "HIGH", "comp");

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> aggregate.execute(cmd));
    }

    @Test
    void testExecute_SlackFailure_ThrowsException() {
        // Arrange
        mockSlack.setShouldFail(true); // Slack returns false
        mockGitHub.setMockUrl("http://url");
        ReportDefectCmd cmd = new ReportDefectCmd("VW-454", "T", "D", "HIGH", "comp");

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> aggregate.execute(cmd));
    }
}
