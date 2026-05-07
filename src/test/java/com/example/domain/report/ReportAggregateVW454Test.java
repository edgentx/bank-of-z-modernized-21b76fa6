package com.example.domain.report;

import com.example.domain.report.model.DefectReportedEvent;
import com.example.domain.report.model.ReportAggregate;
import com.example.domain.report.model.ReportDefectCmd;
import com.example.mocks.MockGithubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for S-FB-1: Validating VW-454.
 * Ensures that when a defect is reported, the resulting Slack notification
 * body contains the GitHub issue URL.
 */
class ReportAggregateVW454Test {

    private MockGithubIssuePort mockGithub;
    private MockSlackNotificationPort mockSlack;
    private ReportAggregate aggregate;

    private static final String REPORT_ID = "report-123";
    private static final String EXPECTED_GITHUB_URL = "https://github.com/bank-of-z/vforce360/issues/454";

    @BeforeEach
    void setUp() {
        mockGithub = new MockGithubIssuePort();
        mockSlack = new MockSlackNotificationPort();
        
        // Configure the mock to return the URL we expect to see in Slack
        mockGithub.setMockUrl(EXPECTED_GITHUB_URL);

        aggregate = new ReportAggregate(REPORT_ID, mockGithub, mockSlack);
    }

    @Test
    void testReportDefect_ShouldCreateGithubIssue_AndPostToSlack() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd(
            "DEFECT-454",
            "Validating VW-454",
            "GitHub URL is missing from Slack body",
            "LOW"
        );

        // Act
        List<DefectReportedEvent> events = aggregate.execute(cmd);

        // Assert
        // 1. Verify GitHub interaction was triggered
        assertTrue(mockGithub.wasIssueCreated("Validating VW-454"), "Github issue should have been created");

        // 2. Verify Slack interaction was triggered
        assertEquals(1, mockSlack.getMessages().size(), "Should have posted one message to Slack");
        assertTrue(mockSlack.lastMessageWasToChannel("#vforce360-issues"), "Message should go to #vforce360-issues");

        // 3. CRITICAL ASSERTION FOR VW-454: Verify the URL is in the Slack Body
        // This is the regression test for the defect.
        assertTrue(
            mockSlack.lastMessageContainsUrl(EXPECTED_GITHUB_URL),
            "Slack body MUST contain the GitHub issue URL (VW-454).\nActual body: " + mockSlack.getMessages().get(0).body
        );

        // 4. Verify Domain Event
        assertEquals(1, events.size());
        assertEquals(REPORT_ID, events.get(0).aggregateId());
        assertEquals(EXPECTED_GITHUB_URL, events.get(0).githubUrl());
    }

    @Test
    void testSlackBodyFormat_ShouldIncludeMarkdownLink() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd(
            "DEFECT-455",
            "Another Defect",
            "Testing formatting",
            "HIGH"
        );
        
        String customUrl = "https://github.com/bank-of-z/vforce360/issues/455";
        mockGithub.setMockUrl(customUrl);

        // Act
        aggregate.execute(cmd);

        // Assert
        String slackBody = mockSlack.getMessages().get(0).body;
        
        // Check for standard slack link formatting <url|text> or just url presence
        // The defect specifically asked for the URL to be present.
        assertTrue(slackBody.contains(customUrl), "URL must be visible in body");
        assertTrue(slackBody.contains("HIGH"), "Severity should be in body");
    }
}
