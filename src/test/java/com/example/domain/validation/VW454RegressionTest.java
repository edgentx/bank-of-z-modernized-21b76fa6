package com.example.domain.validation;

import com.example.ports.SlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.domain.validation.model.ReportDefectCommand;
import com.example.domain.validation.model.ValidationAggregate;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Story: S-FB-1
 * Title: Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * Defect: Validation check to ensure Slack body includes GitHub issue link.
 * 
 * Test Strategy:
 * 1. Red Phase: Tests verify that when a defect is reported, the external services
 *    are called with the expected arguments (specifically the URL in the Slack body).
 * 2. Uses Mock Adapters for Slack and GitHub to ensure no real I/O occurs.
 */
public class VW454RegressionTest {

    /**
     * AC: Regression test added to e2e/regression/ covering this scenario.
     * 
     * Scenario: Valid defect report flow
     * Given: A temporal worker triggers _report_defect
     * When: The ValidationAggregate processes the ReportDefectCommand
     * Then: The GitHub service is invoked to create an issue
     * And: The Slack service is invoked with a body containing the GitHub URL
     */
    @Test
    void testReportDefect_ShouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        MockGitHubIssuePort mockGitHub = new MockGitHubIssuePort();
        MockSlackNotificationPort mockSlack = new MockSlackNotificationPort();
        
        // Configure GitHub mock to return a predictable URL
        URI expectedUrl = URI.create("https://github.com/example/issues/454");
        mockGitHub.setMockUrl(expectedUrl);

        ValidationAggregate aggregate = new ValidationAggregate(mockSlack, mockGitHub);
        
        String defectTitle = "VW-454: Missing GitHub link";
        String defectDescription = "The validation logic does not append the link.";
        ReportDefectCommand cmd = new ReportDefectCommand(defectTitle, defectDescription);

        // Act
        aggregate.execute(cmd);

        // Assert: GitHub creation was triggered
        assertTrue(mockGitHub.wasCreateCalled(), "GitHub create issue should have been triggered");
        assertEquals(defectTitle, mockGitHub.getCapturedTitle());
        assertEquals(defectDescription, mockGitHub.getCapturedDescription());

        // Assert: Slack notification was triggered
        assertTrue(mockSlack.wasNotifyCalled(), "Slack notification should have been triggered");
        
        // Assert: The Actual Behavior - The Slack body contains the specific GitHub URL
        // This currently FAILS (Red Phase) because the implementation (ValidationAggregate) is empty/stubbed.
        String slackBody = mockSlack.getCapturedBody();
        assertNotNull(slackBody, "Slack body should not be null");
        
        boolean containsUrl = slackBody.contains(expectedUrl.toString());
        assertTrue(containsUrl, 
            "Slack body must contain the GitHub issue URL: " + expectedUrl + ". " +
            "Actual body was: [" + slackBody + "]");
    }

    /**
     * Scenario: Handling GitHub failure
     * Given: GitHub service throws an exception
     * When: ReportDefectCommand is executed
     * Then: Slack notification is NOT sent
     * And: Exception is propagated
     */
    @Test
    void testReportDefect_GitHubFailure_ShouldPropagateError() {
        // Arrange
        MockGitHubIssuePort mockGitHub = new MockGitHubIssuePort();
        mockGitHub.setShouldFail(true);
        
        MockSlackNotificationPort mockSlack = new MockSlackNotificationPort();
        ValidationAggregate aggregate = new ValidationAggregate(mockSlack, mockGitHub);
        
        ReportDefectCommand cmd = new ReportDefectCommand("Test", "Test");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> aggregate.execute(cmd));
        assertFalse(mockSlack.wasNotifyCalled(), "Slack should not be notified if GitHub fails");
    }

    /**
     * Scenario: Null Command Handling
     * Ensures the aggregate handles invalid inputs gracefully as per validation rules.
     */
    @Test
    void testReportDefect_NullTitle_ShouldThrowException() {
        // Arrange
        MockGitHubIssuePort mockGitHub = new MockGitHubIssuePort();
        MockSlackNotificationPort mockSlack = new MockSlackNotificationPort();
        ValidationAggregate aggregate = new ValidationAggregate(mockSlack, mockGitHub);
        
        ReportDefectCommand cmd = new ReportDefectCommand(null, "Description");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }
}