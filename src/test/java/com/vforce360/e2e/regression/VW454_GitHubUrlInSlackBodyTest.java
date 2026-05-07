package com.vforce360.e2e.regression;

import com.vforce360.core.DefectReportOrchestrator;
import com.vforce360.mocks.MockGitHubIssueAdapter;
import com.vforce360.mocks.MockSlackNotificationAdapter;
import com.vforce360.ports.github.GitHubIssuePort;
import com.vforce360.ports.slack.SlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for Story ID: S-FB-1
 * Defect: VW-454 — GitHub URL in Slack body (end-to-end)
 *
 * Context: Validating that when a defect is reported, the resulting Slack notification
 * contains the link to the created GitHub issue.
 */
class VW454_GitHubUrlInSlackBodyTest {

    private MockSlackNotificationAdapter mockSlack;
    private MockGitHubIssueAdapter mockGitHub;
    private DefectReportOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        // Instantiate mocks for external dependencies
        mockSlack = new MockSlackNotificationAdapter();
        mockGitHub = new MockGitHubIssueAdapter();

        // Inject mocks into the System Under Test (SUT)
        // Orchestrator is the class handling the temporal-worker logic
        orchestrator = new DefectReportOrchestrator(mockSlack, mockGitHub);
    }

    @Test
    @DisplayName("S-FB-1: Verify Slack body includes GitHub issue link after defect reporting")
    void shouldIncludeGitHubUrlInSlackBody() {
        // Arrange
        String defectTitle = "VW-454 Regression Test";
        String defectDescription = "Validating that the URL is present in the Slack body.";
        String expectedGitHubUrl = "https://github.com/vforce360/core/issues/454";

        // Configure the Mock GitHub adapter to return a specific URL
        mockGitHub.setMockUrl(expectedGitHubUrl);

        // Act
        // Trigger the _report_defect flow via the orchestrator
        orchestrator.reportDefect(defectTitle, defectDescription);

        // Assert
        // 1. Verify that a message was actually sent to Slack
        assertTrue(mockSlack.getSentMessages().size() > 0, "Slack should have received a notification");

        // 2. Verify the message body contains the expected GitHub URL
        String actualSlackBody = mockSlack.getLastMessageBody();
        assertNotNull(actualSlackBody, "Slack body should not be null");

        // CRITICAL ASSERTION: The Slack body MUST contain the GitHub URL
        // This is the core validation for defect VW-454
        assertTrue(
            actualSlackBody.contains(expectedGitHubUrl),
            "Slack body should contain the GitHub issue URL: " + expectedGitHubUrl + "\nActual Body: " + actualSlackBody
        );
    }

    @Test
    @DisplayName("S-FB-1: Verify format of the link in Slack body")
    void shouldFormatGitHubUrlCorrectly() {
        // Arrange
        String defectTitle = "Formatting Check";
        String defectDescription = "Checking angle brackets";
        String expectedGitHubUrl = "https://github.com/vforce360/core/issues/123";
        mockGitHub.setMockUrl(expectedGitHubUrl);

        // Act
        orchestrator.reportDefect(defectTitle, defectDescription);

        // Assert
        String actualSlackBody = mockSlack.getLastMessageBody();
        
        // Expecting the URL to be formatted as a Slack link <URL|Text> or just <URL>
        // This is a common pattern in Slack integration to prevent unfurling issues or ensure visibility
        assertTrue(
            actualSlackBody.contains("<" + expectedGitHubUrl + ">"), 
            "Slack body should contain the URL wrapped in angle brackets for proper formatting."
        );
    }
}
