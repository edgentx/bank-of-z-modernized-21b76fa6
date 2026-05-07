package com.example.domain.validation;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test.
 * S-FB-1: Regression test for defect VW-454.
 * Verifies that the Slack notification body contains the GitHub issue URL.
 */
class ValidationAggregateE2ETest {

    private MockGitHubPort mockGitHub;
    private MockSlackNotificationPort mockSlack;
    private ValidationAggregate aggregate;

    @BeforeEach
    void setUp() {
        mockGitHub = new MockGitHubPort();
        mockSlack = new MockSlackNotificationPort();
        aggregate = new ValidationAggregate("test-val-id", mockGitHub, mockSlack);
    }

    @Test
    void whenReportDefectExecuted_thenSlackBodyContainsGitHubUrl() {
        // Arrange
        String expectedGitHubUrl = "https://github.com/vforce360/shared-infra/issues/1";
        ReportDefectCmd cmd = new ReportDefectCmd(
            "defect-454",
            "Login fails for DB2 users",
            "Connection string is malformed",
            "HIGH",
            "auth-service",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );

        // Act
        List<DefectReportedEvent> events = aggregate.execute(cmd);

        // Assert
        // 1. Verify Aggregate Event
        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(expectedGitHubUrl, events.get(0).githubIssueUrl());

        // 2. Verify Slack Notification Content (S-FB-1)
        String actualSlackBody = mockSlack.getLastMessageBody();
        assertNotNull(actualSlackBody, "Slack message body should not be null");
        
        // Critical assertion: The body MUST contain the URL provided by GitHub
        // If this fails, the defect VW-454 is reproduced.
        assertTrue(
            actualSlackBody.contains(expectedGitHubUrl),
            "Slack body should contain GitHub issue URL.\nExpected to contain: " + expectedGitHubUrl + "\nActual body: " + actualSlackBody
        );

        // 3. Verify it was sent to the correct channel
        assertEquals("vforce360-issues", mockSlack.getLastChannelId());
    }

    @Test
    void whenReportDefectExecuted_GitHubUrlIsPrefixFormatted_correctly() {
        // Arrange
        ReportDefectCmd cmd = new ReportDefectCmd(
            "defect-format",
            "Format check",
            "Desc",
            "LOW",
            "ui",
            "pid"
        );

        // Act
        aggregate.execute(cmd);
        String body = mockSlack.getLastMessageBody();

        // Assert - Verify the format implies "GitHub Issue: <url>"
        // Defect stated: "Slack body includes GitHub issue: <url>"
        assertTrue(body.contains("GitHub Issue:"));
    }
}
