package com.example.domain.defect;

import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.InMemoryGitHubPort;
import com.example.mocks.InMemorySlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Test for S-FB-1.
 * 
 * Context: Validating VW-454 — GitHub URL in Slack body (end-to-end)
 * 
 * Expected Behavior: 
 * When a defect is reported (simulating Temporal workflow trigger),
 * 1. A GitHub issue is created.
 * 2. A Slack message is posted.
 * 3. The Slack message body MUST contain the URL to the created GitHub issue.
 */
public class DefectReportWorkflowTest {

    private InMemoryGitHubPort gitHubPort;
    private InMemorySlackNotificationPort slackPort;
    private DefectReportWorkflowService workflowService;

    @BeforeEach
    void setUp() {
        // Use mock adapters for external dependencies
        gitHubPort = new InMemoryGitHubPort();
        slackPort = new InMemorySlackNotificationPort();
        
        // Inject mocks into the service under test
        workflowService = new DefectReportWorkflowService(gitHubPort, slackPort);
    }

    @Test
    void testSlackBodyContainsGitHubUrl() {
        // Arrange
        String defectTitle = "VW-454: Validation Failure";
        String defectBody = "Critical validation logic missing in shared module.";
        String expectedChannel = "#vforce360-issues";

        // Configure Mock GitHub to return a specific URL
        String expectedGitHubUrl = "https://github.com/bank-of-z/core/issues/454";
        gitHubPort.setNextIssueUrl(expectedGitHubUrl);

        // Act (Triggering the _report_defect workflow equivalent)
        workflowService.reportDefect(defectTitle, defectBody);

        // Assert (Verifying Expected Behavior)
        // 1. Verify GitHub was called (Implicitly done by needing the URL)
        // 2. Verify Slack was called
        assertTrue(slackPort.wasMessageSent(expectedChannel), "Slack message should be sent to the correct channel");

        // 3. CRITICAL ASSERTION: Verify the Slack body contains the GitHub issue link
        String actualSlackMessage = slackPort.getLastMessageBody(expectedChannel);
        assertNotNull(actualSlackMessage, "Slack message body should not be null");
        
        // This assertion specifically addresses the Acceptance Criteria:
        // "Slack body includes GitHub issue: <url>"
        assertTrue(
            actualSlackMessage.contains(expectedGitHubUrl),
            "Slack body must contain the GitHub issue URL. Expected to contain: [" 
            + expectedGitHubUrl + "] but was: [" + actualSlackMessage + "]"
        );
    }

    @Test
    void testReportDefectHandlesNullTitleGracefully() {
        // Arrange
        String invalidTitle = null;
        String validBody = "Some body";

        // Act & Assert
        // We expect the service to handle validation, either by throwing or returning.
        // Since we are in Red phase, we define the behavior we want.
        assertThrows(
            IllegalArgumentException.class,
            () -> workflowService.reportDefect(invalidTitle, validBody),
            "Reporting a defect with a null title should throw IllegalArgumentException"
        );
    }
}
