package com.example.domain.validation;

import com.example.adapters.MockGitHubAdapter;
import com.example.adapters.MockSlackNotifierAdapter;
import com.example.domain.shared.DefectReportedEvent;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifierPort;
import com.example.workflow.ReportDefectWorkflow;
import com.example.workflow.ReportDefectWorkflowImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase for Story S-FB-1.
 * Validates that when a defect is reported, the resulting Slack body
 * contains the link to the created GitHub issue.
 */
class ValidationE2ETest {

    private MockGitHubAdapter gitHub;
    private MockSlackNotifierAdapter slack;
    private ReportDefectWorkflow workflow;

    @BeforeEach
    void setUp() {
        gitHub = new MockGitHubAdapter();
        slack = new MockSlackNotifierAdapter();
        workflow = new ReportDefectWorkflowImpl(gitHub, slack);
    }

    @Test
    void shouldIncludeGitHubIssueUrlInSlackBody_whenReportDefectIsExecuted() {
        // Arrange
        String defectId = "VW-454";
        String description = "Validating VW-454 — GitHub URL in Slack body (end-to-end)";
        String expectedIssueUrl = "https://github.com/example/bank-of-z/issues/454";

        // Configure the mock GitHub adapter to return a specific URL
        gitHub.setMockIssueUrl(expectedIssueUrl);

        // Act
        workflow.reportDefect(defectId, description);

        // Assert
        // 1. Verify GitHub adapter was called (implicitly verified if we cared about inputs)
        // 2. Verify Slack adapter was called
        assertTrue(slack.wasCalled(), "Slack notifier should have been triggered");

        // 3. Verify the Slack body contains the GitHub URL
        // This is the core assertion for Story S-FB-1
        String actualSlackBody = slack.getCapturedBody();
        assertNotNull(actualSlackBody, "Slack body should not be null");
        assertTrue(
            actualSlackBody.contains(expectedIssueUrl),
            "Slack body should contain the GitHub issue URL. Expected: " + expectedIssueUrl + " in body: " + actualSlackBody
        );
    }

    @Test
    void shouldThrowException_ifGitHubAdapterFails() {
        // Arrange
        String defectId = "VW-999";
        String description = "Critical failure";
        gitHub.setShouldFail(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> workflow.reportDefect(defectId, description));
    }
}
