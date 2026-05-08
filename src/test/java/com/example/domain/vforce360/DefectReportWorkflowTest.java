package com.example.domain.vforce360;

import com.example.mocks.MockGitHubIssuePort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test.
 * Story: VW-454 — GitHub URL in Slack body (end-to-end).
 * 
 * Tests the _report_defect workflow logic (executed by temporal-worker).
 * We verify that when a defect is reported:
 * 1. A GitHub issue is created.
 * 2. The resulting URL is included in the Slack notification body.
 */
public class DefectReportWorkflowTest {

    private MockGitHubIssuePort mockGitHub;
    private MockSlackNotificationPort mockSlack;

    // Class under test (Simulation of Temporal Activity/Workflow logic)
    private DefectReportWorkflow workflow;

    @BeforeEach
    void setUp() {
        mockGitHub = new MockGitHubIssuePort();
        mockSlack = new MockSlackNotificationPort();
        // Inject mocks into the workflow handler
        workflow = new DefectReportWorkflow(mockGitHub, mockSlack);
    }

    @Test
    void testReportDefect_shouldPostSlackMessageContainingGitHubUrl() {
        // Arrange
        String defectTitle = "VW-454: Validation Error";
        String defectDescription = "Validation logic failed...";
        String expectedGitHubUrl = "https://github.com/example-bank/project/issues/454";
        
        // Configure the mock to return a specific URL when issue is created
        mockGitHub.setNextIssueUrl(expectedGitHubUrl);

        // Act
        // Trigger the report_defect flow via temporal-worker exec simulation
        workflow.reportDefect(defectTitle, defectDescription);

        // Assert - Expected Behavior: Slack body includes GitHub issue: <url>
        String actualSlackBody = mockSlack.lastMessageBody;
        
        assertNotNull(actualSlackBody, "Slack message body should not be null");
        // The core defect fix: the URL must appear in the body
        assertTrue(
            actualSlackBody.contains(expectedGitHubUrl),
            "Slack body must contain the GitHub issue URL. Expected: " + expectedGitHubUrl + " in body: " + actualSlackBody
        );
    }

    @Test
    void testReportDefect_shouldPostToCorrectChannel() {
        // Arrange
        String defectTitle = "S-FB-1: Fix Required";
        String defectDescription = "See description";

        // Act
        workflow.reportDefect(defectTitle, defectDescription);

        // Assert
        assertEquals("#vforce360-issues", mockSlack.lastChannel, "Should post to the specific project issues channel");
    }
}
