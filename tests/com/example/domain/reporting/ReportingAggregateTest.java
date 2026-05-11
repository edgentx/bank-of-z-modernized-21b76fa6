package com.example.domain.reporting;

import com.example.domain.reporting.model.ReportDefectCmd;
import com.example.mocks.MockExternalDefectSystemAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for Reporting Aggregate.
 * Validating the defect reporting logic end-to-end within the domain context.
 */
class ReportingAggregateTest {

    private MockExternalDefectSystemAdapter mockAdapter;
    private ReportingAggregate aggregate;

    @BeforeEach
    void setUp() {
        mockAdapter = new MockExternalDefectSystemAdapter();
        aggregate = new ReportingAggregate("test-report-1", mockAdapter);
    }

    @Test
    void shouldGenerateGitHubUrlAndIncludeInSlackNotification() {
        // Given
        String defectId = "VW-454";
        String title = "Fix: Validating VW-454 — GitHub URL in Slack body";
        String description = "Severity: LOW";
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, title, description, "LOW");

        // When
        var events = aggregate.execute(cmd);

        // Then
        // 1. Verify an event was produced
        assertFalse(events.isEmpty(), "Should produce a DefectReportedEvent");

        // 2. Verify GitHub was called
        assertEquals(1, mockAdapter.githubTitlesCreated.size(), "Should create one GitHub issue");
        assertEquals(title, mockAdapter.githubTitlesCreated.get(0), "GitHub title should match command title");

        // 3. Verify Slack was called
        assertEquals(1, mockAdapter.slackMessagesSent.size(), "Should send one Slack notification");

        // 4. Verify the URL link in the Slack body (Regression Test for VW-454)
        // The Mock generates URL "https://github.com/bank-of-z/issues/100"
        String expectedUrl = "https://github.com/bank-of-z/issues/100";
        assertTrue(
            mockAdapter.wasUrlSentToSlack(expectedUrl),
            "Slack body MUST contain the generated GitHub URL. Actual: " + mockAdapter.slackMessagesSent.get(0)
        );

        // 5. Verify the Event state contains the URL for downstream consistency
        String eventUrl = events.get(0).githubIssueUrl();
        assertEquals(expectedUrl, eventUrl, "Event should persist the GitHub URL");
    }

    @Test
    void shouldHandleMultipleDefectReportsUniquely() {
        // Given
        ReportDefectCmd cmd1 = new ReportDefectCmd("1", "Issue 1", "Desc", "LOW");
        ReportDefectCmd cmd2 = new ReportDefectCmd("2", "Issue 2", "Desc", "LOW");

        // When
        aggregate.execute(cmd1);
        aggregate.execute(cmd2);

        // Then
        assertEquals(2, mockAdapter.slackMessagesSent.size());
        // Ensure distinct URLs were generated and sent
        String msg1 = mockAdapter.slackMessagesSent.get(0);
        String msg2 = mockAdapter.slackMessagesSent.get(1);
        assertNotEquals(msg1, msg2, "Slack messages should be unique per issue URL");
        assertTrue(msg1.contains("/100"));
        assertTrue(msg2.contains("/101"));
    }
}
