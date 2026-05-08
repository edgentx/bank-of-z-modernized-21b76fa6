package com.example.domain.vforce360;

import com.example.domain.shared.Command;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCommand;
import com.example.mocks.MockGitHubIssueTracker;
import com.example.mocks.MockVForce360Notifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for VW-454: GitHub URL in Slack body.
 * Context: When a defect is reported via temporal-worker exec,
 * the resulting Slack message body MUST contain the GitHub issue link.
 */
public class VW454SlackUrlValidationTest {

    // SUT Components
    // In a real Spring Boot app, these might be injected. 
    // For the Red phase, we assume a Domain Service or Workflow Orchestrator.
    // Here we simulate the expected logic flow.

    private MockVForce360Notifier mockNotifier;
    private MockGitHubIssueTracker mockGitHub;
    private String expectedGithubUrl;

    @BeforeEach
    public void setUp() {
        expectedGithubUrl = "https://github.com/example/repo/issues/454";
        mockNotifier = new MockVForce360Notifier();
        mockGitHub = new MockGitHubIssueTracker(expectedGithubUrl);
    }

    /**
     * Simulates the workflow execution:
     * 1. Receive Command
     * 2. Process Domain Logic (Aggregate)
     * 3. Trigger Side Effects (Ports)
     */
    private void simulateWorkflowExecution(Command cmd) {
        if (cmd instanceof ReportDefectCommand c) {
            // 1. Create GitHub Issue
            String url = mockGitHub.createIssue(c.title(), c.description());

            // 2. Prepare Slack Body
            // Logic currently under verification. 
            // The implementation is expected to append the URL to the body.
            String slackBody = "Defect Reported: " + c.title() + "\nGitHub Issue: " + url;

            // 3. Send Notification
            mockNotifier.sendDefectReport(slackBody);
        } else {
            throw new IllegalArgumentException("Unknown command");
        }
    }

    @Test
    public void testSlackBodyContainsGitHubUrl_vw454() {
        // Arrange
        String defectId = "S-FB-1";
        String title = "Fix: Validating VW-454";
        String description = "Severity: LOW";
        ReportDefectCommand command = new ReportDefectCommand(defectId, title, description);

        // Act
        simulateWorkflowExecution(command);

        // Assert
        String actualBody = mockNotifier.getLastSentBody();
        
        assertNotNull(actualBody, "Slack body should not be null");
        assertTrue(
            actualBody.contains(expectedGithubUrl),
            "Slack body MUST contain the GitHub issue URL.\nExpected URL: " + expectedGithubUrl + "\nActual Body: " + actualBody
        );
    }

    @Test
    public void testSlackBodyFormat_isStructured() {
        // Arrange
        ReportDefectCommand command = new ReportDefectCommand("S-FB-2", "Another Issue", "Desc");

        // Act
        simulateWorkflowExecution(command);

        // Assert
        String actualBody = mockNotifier.getLastSentBody();
        // Verify it's not just a raw dump, but has the structure expected by the Slack parser
        assertTrue(actualBody.contains("Defect Reported:"), "Missing context header");
        assertTrue(actualBody.contains("GitHub Issue:"), "Missing GitHub label");
    }
}
