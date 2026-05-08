package com.example.e2e.regression;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for S-FB-1 / VW-454.
 * 
 * Validates that when a defect is reported:
 * 1. A GitHub issue is created.
 * 2. The Slack notification body contains the specific URL to that GitHub issue.
 * 
 * This test captures the 'Red' phase expectations.
 */
public class VW454SlackUrlValidationTest {

    private MockGitHubIssuePort mockGithub;
    private MockSlackNotificationPort mockSlack;
    private ValidationAggregate aggregate;

    private static final String VALIDATION_ID = "val-123";
    private static final String DEFECT_ID = "VW-454";
    private static final String EXPECTED_URL = "https://github.com/bank-of-z/issues/454";

    @BeforeEach
    void setUp() {
        mockGithub = new MockGitHubIssuePort();
        mockSlack = new MockSlackNotificationPort();
        aggregate = new ValidationAggregate(VALIDATION_ID, mockGithub, mockSlack);
    }

    @Test
    void whenGitHubIssueCreated_slackBodyMustContainIssueLink() {
        // Arrange
        mockGithub.mockCreateIssueResult(EXPECTED_URL);
        ReportDefectCmd cmd = new ReportDefectCmd(
            DEFECT_ID,
            "Fix: Validating VW-454",
            "Defect reported by user."
        );

        // Act
        // In the real implementation, this orchestrates the Temporal workflow logic.
        // For domain unit test, we execute the command on the aggregate.
        aggregate.execute(cmd);

        // Assert
        // 1. Verify GitHub interaction
        assertNotNull(mockGithub.getLastTitle(), "GitHub port should have been called");
        assertEquals("Fix: Validating VW-454", mockGithub.getLastTitle());

        // 2. Verify Slack interaction
        String slackBody = mockSlack.getLastMessage();
        assertNotNull(slackBody, "Slack port should have been called");

        // 3. Validate URL presence (Critical for S-FB-1)
        // Expected Behavior: Slack body includes GitHub issue: <url>
        assertTrue(
            slackBody.contains(EXPECTED_URL),
            "Slack body MUST contain the specific GitHub Issue URL returned by the GitHub port.\nFound: " + slackBody
        );
        
        // Ensure the link format is explicitly present as requested by the defect story
        assertTrue(
            slackBody.contains("GitHub Issue:"),
            "Slack body should contain a label 'GitHub Issue:' before the link for clarity."
        );
    }

    @Test
    void whenGitHubCreationFails_validationShouldPropagateError() {
        // Arrange
        // Simulate GitHub failure by returning empty optional
        mockGithub.mockCreateIssueResult(null); 
        ReportDefectCmd cmd = new ReportDefectCmd(
            DEFECT_ID,
            "Test Failure",
            "Description"
        );

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("Failed to create GitHub issue"));
        
        // Verify Slack was NOT called if GitHub failed (Orchestration integrity)
        assertNull(mockSlack.getLastMessage(), "Slack should NOT be notified if GitHub issue creation fails.");
    }
}
