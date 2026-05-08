package com.example.e2e.regression;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test for Defect VW-454.
 * 
 * Defect: When triggering _report_defect via temporal-worker,
 * the resulting Slack message body does not contain the GitHub issue link.
 * 
 * Expected Behavior: Slack body includes GitHub issue: <url>
 * 
 * Component: validation / temporal-worker-logic
 */
public class VW454SlackGitHubLinkRegressionTest {

    private MockSlackPort mockSlack;
    private MockGitHubPort mockGitHub;
    private ReportDefectWorkflow reportDefectWorkflow; // System Under Test

    @BeforeEach
    void setUp() {
        mockSlack = new MockSlackPort();
        mockGitHub = new MockGitHubPort();
        
        // In the Red phase, this class likely doesn't exist yet or throws exceptions.
        // We assume a workflow/service class that handles the logic.
        try {
            // Using reflection or direct instantiation depending on if we are in the same package.
            // For now, we assume a simple wrapper exists.
            reportDefectWorkflow = new ReportDefectWorkflow(mockSlack, mockGitHub);
        } catch (NoClassDefFoundError | Exception e) {
            // In the true Red phase, we might just create a stub or let the test fail at compilation.
            // Since this is text output, we will assume the shell exists for the sake of the test structure.
            reportDefectWorkflow = new ReportDefectWorkflow(mockSlack, mockGitHub);
        }
    }

    @Test
    void testReportDefect_ShouldIncludeGitHubUrlInSlackMessage() {
        // Arrange
        String defectTitle = "VW-454: GitHub URL in Slack body missing";
        String defectDescription = "Reproduction steps...";
        String expectedGitHubUrl = "https://github.com/project/issues/454";
        String targetChannel = "C12345";

        // Configure the GitHub mock to return a valid URL
        mockGitHub.setMockReturnUrl(expectedGitHubUrl);

        // Act
        // This method encapsulates the temporal worker logic: create GH issue -> notify Slack
        reportDefectWorkflow.execute(defectTitle, defectDescription, targetChannel);

        // Assert
        // 1. Verify GitHub interaction happened (implies issue was created)
        assertTrue(mockGitHub.wasIssueCreatedWithTitle(defectTitle), 
            "Issue should be created in GitHub");

        // 2. Verify Slack interaction happened
        assertNotNull(mockSlack.getLastMessageContent(), 
            "Slack message should be posted");
        assertEquals(targetChannel, mockSlack.getLastChannelId(), 
            "Slack message should go to the correct channel");

        // 3. CRITICAL ASSERTION for VW-454: Verify the URL is IN the body
        // This is the specific regression check.
        assertTrue(mockSlack.messageContains(expectedGitHubUrl), 
            "Regression VW-454: Slack body must contain the GitHub issue URL. " +
            "Expected ['" + expectedGitHubUrl + "'] inside message ['" + mockSlack.getLastMessageContent() + "']");
    }

    @Test
    void testReportDefect_ShouldFailIfGitHubUrlIsNull() {
        // Arrange
        mockGitHub.setMockReturnUrl(null); // Simulate a failure or empty response from GitHub

        // Act & Assert
        // The system should handle the null case, but specifically,
        // it should NOT post a message claiming success without a link, 
        // or it should throw an exception.
        assertThrows(IllegalStateException.class, () -> {
            reportDefectWorkflow.execute("Null Test", "Desc", "C12345");
        }, "Workflow should fail gracefully if GitHub returns a null URL");
    }
}
