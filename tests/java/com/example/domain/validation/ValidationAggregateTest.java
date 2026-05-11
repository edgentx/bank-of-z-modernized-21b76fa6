package com.example.domain.validation;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.validation.MockGitHubPort;
import com.example.mocks.validation.MockNotificationPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase for VW-454.
 * Verifies that the Slack notification body contains the GitHub issue URL.
 */
public class ValidationAggregateTest {

    @Test
    public void testReportDefect_generatesGitHubUrl() {
        // Arrange
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/example/bank-of-z/issues/454";

        MockGitHubPort mockGitHub = new MockGitHubPort();
        mockGitHub.setMockUrl(expectedUrl);

        MockNotificationPort mockSlack = new MockNotificationPort();

        ValidationAggregate aggregate = new ValidationAggregate(defectId, mockGitHub, mockSlack);
        ReportDefectCmd cmd = new ReportDefectCmd(defectId);

        // Act
        var events = aggregate.execute(cmd);

        // Assert
        assertFalse(events.isEmpty(), "Should emit an event");
        assertTrue(events.get(0) instanceof DefectReportedEvent, "Should be DefectReportedEvent");
        assertEquals(expectedUrl, ((DefectReportedEvent) events.get(0)).githubUrl(), "Event should contain GitHub URL");
    }

    @Test
    public void testReportDefect_includesUrlInSlackBody() {
        // Arrange
        String defectId = "VW-454";
        String expectedUrl = "https://github.com/example/bank-of-z/issues/454";
        String expectedBodyContains = "GitHub issue: <" + expectedUrl + ">";

        MockGitHubPort mockGitHub = new MockGitHubPort();
        mockGitHub.setMockUrl(expectedUrl);

        MockNotificationPort mockSlack = new MockNotificationPort();

        ValidationAggregate aggregate = new ValidationAggregate(defectId, mockGitHub, mockSlack);
        ReportDefectCmd cmd = new ReportDefectCmd(defectId);

        // Act
        aggregate.execute(cmd);

        // Assert - Verify the Slack body received the URL
        String actualSlackBody = mockSlack.getLastPayload();
        assertNotNull(actualSlackBody, "Slack payload should not be null");
        assertTrue(
            actualSlackBody.contains(expectedUrl),
            "Slack body must contain the GitHub URL. Expected to contain: " + expectedUrl + " but was: " + actualSlackBody
        );
    }

    @Test
    public void testReportDefect_throwsIfGitHubUrlMissing() {
        // Arrange
        String defectId = "VW-999";
        MockGitHubPort mockGitHub = new MockGitHubPort(); // Returns null/blank by default if not set
        MockNotificationPort mockSlack = new MockNotificationPort();
        ValidationAggregate aggregate = new ValidationAggregate(defectId, mockGitHub, mockSlack);
        ReportDefectCmd cmd = new ReportDefectCmd(defectId);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> aggregate.execute(cmd));
        assertTrue(exception.getMessage().contains("valid URL"));
    }
}
