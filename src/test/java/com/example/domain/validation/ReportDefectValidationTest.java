package com.example.domain.validation;

import com.example.domain.ports.SlackNotifier;
import com.example.domain.ports.GitHubRepository;
import com.example.domain.validation.model.ReportDefectCommand;
import com.example.mocks.MockSlackNotifier;
import com.example.mocks.MockGitHubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test Suite
 * Story: S-FB-1 / VW-454
 * 
 * Verifies that when a defect is reported, the resulting notification
 * contains the valid GitHub issue URL.
 */
class ReportDefectValidationTest {

    private MockGitHubRepository mockGitHubRepo;
    private MockSlackNotifier mockSlackNotifier;
    private ReportDefectService service;

    @BeforeEach
    void setUp() {
        mockGitHubRepo = new MockGitHubRepository();
        mockSlackNotifier = new MockSlackNotifier();
        service = new ReportDefectService(mockGitHubRepo, mockSlackNotifier);
    }

    @Test
    void shouldIncludeGitHubIssueUrlInSlackNotificationBody() {
        // Arrange
        String expectedTitle = "VW-454 Validation Error";
        String expectedUrl = "https://github.com/fake-org/issues/454";
        
        // Configure the Mock GitHub Adapter to return a specific URL
        mockGitHubRepo.setNextIssueUrl(expectedUrl);

        ReportDefectCommand command = new ReportDefectCommand(
            "VW-454", 
            expectedTitle, 
            "Validation logic is broken."
        );

        // Act
        // This would normally be triggered by temporal-worker exec
        service.handleReportDefect(command);

        // Assert
        // 1. Verify the service actually called the mock adapters
        assertTrue(mockSlackNotifier.wasCalled(), "Slack notifier should have been triggered");
        
        // 2. Verify the Slack body contains the link line
        String actualBody = mockSlackNotifier.getLastBody();
        assertNotNull(actualBody, "Slack body should not be null");
        
        // The critical validation for VW-454
        // Expected format: "GitHub Issue: <url>"
        assertTrue(
            actualBody.contains(expectedUrl), 
            "Slack body must contain the GitHub Issue URL. Actual body: " + actualBody
        );

        // Verify the specific format expected by the story (Github issue: <url>)
        assertTrue(
            actualBody.matches("(?i).*github issue:.*" + expectedUrl + ".*"),
            "Slack body must label the URL as 'GitHub issue'"
        );
    }

    @Test
    void shouldFailIfGitHubUrlIsMissingFromBody() {
        // This test explicitly enforces the "Expected Behavior" vs "Actual Behavior" gap.
        // If the service returns a body without the URL, this test fails.

        // Arrange
        mockGitHubRepo.setNextIssueUrl("https://github.com/fake-org/issues/999");
        ReportDefectCommand command = new ReportDefectCommand("VW-999", "Missing URL", "...");

        // Act
        service.handleReportDefect(command);

        // Assert
        String body = mockSlackNotifier.getLastBody();
        
        // This assertion represents the fix for the defect.
        // If the implementation is empty/broken, body will be null or empty.
        assertNotEquals("", body, "Body should not be empty");
        
        // If the GitHub step was skipped, this fails.
        assertTrue(body.contains("https://github.com"), "Body must contain a GitHub link");
    }
}
